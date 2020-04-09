package scw.mvc.http;

import java.io.IOException;

import scw.mvc.Response;
import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;

public interface HttpResponse extends Response{
    void addCookie(HttpCookie cookie);
    
    void addCookie(String name, String value);

    void sendError(int sc, String msg) throws IOException;

    void sendError(int sc) throws IOException;

    void sendRedirect(String location) throws IOException;

    void setStatus(int sc);

    int getStatus();

    void setContentLength(long length);

    HttpHeaders getHeaders();
}
