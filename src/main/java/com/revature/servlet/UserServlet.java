package com.revature.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.model.User;
import com.revature.p1.orm.persistence.SQLOperationHandler;
import com.revature.p1.orm.util.Configuration;
import com.revature.service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    UserService objThisServlet;

    public UserServlet() throws SQLException {
        this.objThisServlet = new UserService(new ObjectMapper(),new SQLOperationHandler());
        Configuration.addAnnotatedClass(User.class);
    }

    /**
     * Flag: 0.
     * Requests a query for a single row, to be returned in json format.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        objThisServlet.doAnything(req, res, 0);
    }

    /**
     * Flag: 1.
     * Requests an insert for a single row whose fields are described either in the request's params, or in its body in json format.
     * Servlet should construct an object holding the input fields, persist it then return a json of its persisted fields to reassure client that insert was performed properly.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        objThisServlet.doAnything(req, res, 1);
    }

    /**
     * Flag: 2
     * Requests an update for a single row whose fields are described either in the request's params, or in its body in json format.
     * Servlet should construct an object holding the input fields, find the row with the same primary key ID in the users table, update it then return a json of the updated fields to reassure client that update was performed properly.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) {
        objThisServlet.doAnything(req, res, 2);
    }

    /**
     * Flag: 3
     * Requests deletion of a single row with fields matching those described either in the request's params, or in its body in json format.
     * Servlet should use the input fields to attempt to infer which row the client wants deleted. (The primary key makes this easy, but is not required.) If exactly one row is found to match the input fields provided, it is deleted. If more than one row matches the input field (possibly if too few fields were provided and multiple rows happen to share the same values in all of them), the servlet refuses to delete any.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) {
        objThisServlet.doAnything(req, res, 3);
    }
}
