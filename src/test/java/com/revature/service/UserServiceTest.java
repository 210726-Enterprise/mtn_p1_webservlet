package com.revature.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.model.User;
import com.revature.p1.orm.persistence.SQLOperationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserService objService;

    SQLOperationHandler objHandlerMock;
    ObjectMapper objMapperMock;
    User objUserMockInput;
    Optional objUserMockQueryOutput;
    HttpServletRequest objReqMock;
    HttpServletResponse objRespMock;
    String str;
    ObjectWriter objWriterMock;
    BufferedReader objReaderMock;
    Stream<String> strReqStream;
    String strBuiltInput;
    ServletOutputStream objServOutMock;

    @BeforeEach
    void init() {
        objHandlerMock = mock(SQLOperationHandler.class);
        objMapperMock = mock(ObjectMapper.class);
        objReqMock = mock(HttpServletRequest.class);
        objRespMock = mock(HttpServletResponse.class);
        objWriterMock = mock(ObjectWriter.class);
        str = "whatever";
        objReaderMock = mock(BufferedReader.class);
        objServOutMock = mock(ServletOutputStream.class);
        objUserMockInput = mock(User.class);

        objService = new UserService(objMapperMock, objHandlerMock);
        strReqStream = Arrays.stream(new String[]{"{",
                "    \"id\" : 1,",
                "    \"firstname\" : \"test\"",
                "    \"lastname\" : \"test2\"",
                "    \"emailaddress\" : \"test3@test.com\"",
                "}"});
        strBuiltInput = "{"
                + "    \"id\" : 1,"
                + "    \"firstname\" : \"test\""
                + "    \"lastname\" : \"test2\""
                + "    \"emailaddress\" : \"test3@test.com\""
                + "}";
        objUserMockQueryOutput = Optional.of(new User(1, "test1", "test2", "test3@test.com"));
    }

    @Test
    void doGet_FromBody() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        objUserMockInput = new User(1, "test1", "test2", "test3@test.com");

        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(strReqStream);
        when(objMapperMock.readValue(eq(strBuiltInput), eq(User.class))).thenReturn(objUserMockInput);

        when(objHandlerMock.retrieveByID(objUserMockInput.getId(), User.class)).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock, 0);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doGet_FromBody_BadId() throws IOException {
        objUserMockInput = new User(0, "test1", "test2", "test3@test.com");
        strReqStream = Arrays.stream(new String[]{"{",
                "    \"id\" : 0,",
                "    \"firstname\" : \"test\"",
                "    \"lastname\" : \"test2\"",
                "    \"emailaddress\" : \"test3@test.com\"",
                "}"});
        strBuiltInput = "{"
                + "    \"id\" : 0,"
                + "    \"firstname\" : \"test\""
                + "    \"lastname\" : \"test2\""
                + "    \"emailaddress\" : \"test3@test.com\""
                + "}";

        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(strReqStream);
        when(objMapperMock.readValue(eq(strBuiltInput), eq(User.class))).thenReturn(objUserMockInput);

        objService.doAnything(objReqMock, objRespMock, 0);
        verify(objRespMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doGet_FromBody_NotFound() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        objUserMockInput = new User(1, "test1", "test2", "test3@test.com");

        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(strReqStream);
        when(objMapperMock.readValue(eq(strBuiltInput), eq(User.class))).thenReturn(objUserMockInput);

        when(objHandlerMock.retrieveByID(1, User.class)).thenReturn(Optional.empty());

        objService.doAnything(objReqMock, objRespMock, 0);
        verify(objRespMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verifyNoMoreInteractions(objRespMock);
    }


    @Test
    void doGet_FromParams() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("test2");
        when(objReqMock.getParameter("emailaddress")).thenReturn("test3@test.com");

        when(objHandlerMock.retrieveByID(1, User.class)).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock,objRespMock,0);
        verify(objServOutMock).print(str);
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPost_FromBody_AllFieldsFilled() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(strReqStream);
        when(objMapperMock.readValue(any(String.class), eq(User.class))).thenReturn(objUserMockInput);

        when(objHandlerMock.persistThis(any(User.class))).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock,1);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPost_FromParams_AllFieldsFilled() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("test2");
        when(objReqMock.getParameter("emailaddress")).thenReturn("test3@test.com");

        when(objHandlerMock.persistThis(any(User.class))).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock,1);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPost_FromParams_NoId_ShouldBeOK() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn(null);
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("test2");
        when(objReqMock.getParameter("emailaddress")).thenReturn("test3@test.com");

        when(objHandlerMock.persistThis(any(User.class))).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock,1);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPost_FromParams_SomeNonPKColumnsNull() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("null");
        when(objReqMock.getParameter("emailaddress")).thenReturn("null");

        when(objHandlerMock.persistThis(any(User.class))).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock,1);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPut_FromParams_AllFieldsFilled() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("test2");
        when(objReqMock.getParameter("emailaddress")).thenReturn("test3@test.com");

        when(objHandlerMock.update(any(User.class))).thenReturn(objUserMockQueryOutput);
        when(objMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objWriterMock);
        when(objMapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objUserMockQueryOutput.get())).thenReturn(str);
        when(objRespMock.getOutputStream()).thenReturn(objServOutMock);

        objService.doAnything(objReqMock, objRespMock,2);
        verify(objServOutMock).print(eq(str));
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPut_FromParams_NullInput() throws IOException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn(null);
        when(objReqMock.getParameter("firstname")).thenReturn(null);
        when(objReqMock.getParameter("lastname")).thenReturn(null);
        when(objReqMock.getParameter("emailaddress")).thenReturn(null);

        objService.doAnything(objReqMock, objRespMock,2);
        verify(objRespMock).setStatus(HttpServletResponse.SC_CONFLICT);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doPut_FromParams_PKOnlyColumnNotNull() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn(null);
        when(objReqMock.getParameter("lastname")).thenReturn(null);
        when(objReqMock.getParameter("emailaddress")).thenReturn(null);

        when(objHandlerMock.update(any(User.class))).thenThrow(new NoSuchElementException());

        objService.doAnything(objReqMock, objRespMock,2);
        verify(objRespMock).setStatus(422);
        verifyNoMoreInteractions(objRespMock);
    }

    @Test
    void doDelete_FromParams_AllFieldsFilled() throws IOException, SQLException, InvocationTargetException, IllegalAccessException {
        when(objReqMock.getReader()).thenReturn(objReaderMock);
        when(objReqMock.getReader().lines()).thenReturn(Stream.empty());
        when(objReqMock.getParameter("id")).thenReturn("1");
        when(objReqMock.getParameter("firstname")).thenReturn("test1");
        when(objReqMock.getParameter("lastname")).thenReturn("test2");
        when(objReqMock.getParameter("emailaddress")).thenReturn("test3@test.com");

        when(objHandlerMock.delete(any(User.class))).thenReturn(true);

        objService.doAnything(objReqMock, objRespMock,3);
        verify(objRespMock).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(objRespMock);
    }
}
