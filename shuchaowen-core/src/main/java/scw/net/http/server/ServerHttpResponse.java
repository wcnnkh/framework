package scw.net.http.server;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;
import scw.net.message.OutputMessage;

public interface ServerHttpResponse extends Flushable, OutputMessage{
    void addCookie(HttpCookie cookie);
    
    void addCookie(String name, String value);

    void sendError(int sc) throws IOException;
    
    void sendError(int sc, String msg) throws IOException;

    void sendRedirect(String location) throws IOException;

    void setStatusCode(HttpStatus httpStatus);
    
    void setStatus(int sc);

    int getStatus();

    void setContentLength(long length);

    HttpHeaders getHeaders();
    
    String getRawContentType();
	
	void setContentType(String contentType);
	
	String getCharacterEncoding();

	PrintWriter getWriter() throws IOException;
	
	boolean isCommitted();
}
