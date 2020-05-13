package scw.mvc.http;

import java.io.IOException;

import scw.mvc.ServerResponse;
import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;

public interface ServerHttpResponse extends ServerResponse{
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
}
