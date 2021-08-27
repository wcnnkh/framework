package io.basc.framework.web;

import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpStatus;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

public interface ServerHttpResponse extends HttpOutputMessage, Closeable, Flushable{
    void addCookie(HttpCookie cookie);
    
    void addCookie(String name, String value);

    void sendError(int sc) throws IOException;
    
    void sendError(int sc, String msg) throws IOException;

    void sendRedirect(String location) throws IOException;

    void setStatusCode(HttpStatus httpStatus);
    
    void setStatus(int sc);

    int getStatus();
    
    PrintWriter getWriter() throws IOException;

	boolean isCommitted();
}
