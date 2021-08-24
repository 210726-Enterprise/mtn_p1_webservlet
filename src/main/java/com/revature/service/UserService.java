package com.revature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.model.User;
import com.revature.p1.orm.persistence.SQLOperationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service-layer class that handles HTTP requests related to the User class (or the users table).
 */
public class UserService {
    private static final Logger lLog4j = LoggerFactory.getLogger(UserService.class);

    private final ObjectMapper objMapper;
    private final SQLOperationHandler objHandler;

    public UserService(ObjectMapper objMapper, SQLOperationHandler objHandler) {
        this.objMapper = objMapper;
        this.objHandler = objHandler;
    }

    /**
     * Maps the body of an HTTP request (or its parameters, if body is empty) to a User instance for interpretation by the doAnything() method.
     * @param req HTTP Request.
     * @return A User instance, with fields set to values specified by the request.
     * @throws IOException
     */
    public User createUserFromRequest(HttpServletRequest req) throws IOException {
        StringBuilder strRequest = new StringBuilder();
        User objNewUser;
        req.getReader().lines()
                .collect(Collectors.toList())
                .forEach(strRequest::append);
        if (strRequest.toString().isEmpty()) {
            objNewUser = new User();
            if (req.getParameter("id") != null && !req.getParameter("id").equals("")) {
                objNewUser.setId(Integer.parseInt(req.getParameter("id")));
            }
            objNewUser.setFirstname(req.getParameter("firstname"));
            objNewUser.setLastname(req.getParameter("lastname"));
            objNewUser.setEmailaddress(req.getParameter("emailaddress"));
            return objNewUser;
        }
        objNewUser = objMapper.readValue(strRequest.toString(), User.class);
        return objNewUser;
    }

    /**
     * Switchboard method that passes data from the HTTP Request as parameters into the ORM operation that corresponds to the HTTP Method requested.
     * Also decides the appropriate HTTP Response status code and whether to print to response body.
     * @param req HTTP Request.
     * @param res Pointer to HTTP Response.
     * @param iDoWhatThough int flag indicating which HTTP Method was requested.
     */
    public void doAnything(HttpServletRequest req, HttpServletResponse res, int iDoWhatThough) {
        try {
            User objMockUser = createUserFromRequest(req);
            Optional<?> objReturn = Optional.empty();
            switch (iDoWhatThough) {
                case 0:
                    if (objMockUser.getId() > 0) {
                        objReturn = objHandler.retrieveByID(objMockUser.getId(),User.class);
                    }
                    break;
                case 1:
                    objReturn = objHandler.persistThis(objMockUser);
                    break;
                case 2:
                    if (objMockUser.getId() > 0) {
                        objReturn = objHandler.update(objMockUser);
                    }
                    break;
                case 3:
                    objReturn = Optional.of(objHandler.delete(objMockUser));
            }
            if (objReturn.isPresent()) {
                if (iDoWhatThough != 3) {
                    String strJson = objMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(objReturn.get());
                    res.getOutputStream().print(strJson);
                    res.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                if ((Boolean)objReturn.get()) {
                    res.setStatus(HttpServletResponse.SC_OK);
                }
            } else {
                switch (iDoWhatThough) {
                    case 0:
                        lLog4j.debug("An HTTP Request for GET processed, returning 404 - Not Found.");
                        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    case 1:
                        lLog4j.debug("An HTTP Request for POST processed, returning 500 - Internal Server Error suggesting SQL database failed to perform row insert but did not throw back SQLException.");
                        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    default:
                        lLog4j.debug("An HTTP Request processed, returning 409 - Conflict suggesting operation failed in non-fatal way.");
                        res.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
        } catch (BatchUpdateException e) {
            lLog4j.debug("An HTTP Request for DELETE processed, returning 406 - Not Acceptable due to request attempting to delete multiple rows at once.");
            res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        } catch (NoSuchElementException e) {
            if (iDoWhatThough == 2) {
                // 422: The servlet understands the request, but cannot process the instructions.
                lLog4j.debug("An HTTP Request for PUT processed, returning 422 - Unprocessable Entity suggesting request params/body failed to contain any non-null values for any non-primary key columns in the users table.");
                res.setStatus(422);
            } else {
                lLog4j.error(e.getMessage());
                lLog4j.error("An HTTP Request processed, throwing a NoSuchElementException in a context theoretically impossible via this servlet!!");
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            lLog4j.debug(e.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}