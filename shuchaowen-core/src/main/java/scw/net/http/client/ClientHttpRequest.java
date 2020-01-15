package scw.net.http.client;

import java.io.IOException;

import scw.net.ClientRequest;
import scw.net.http.HttpHeaders;
import scw.net.http.Method;

public interface ClientHttpRequest extends ClientRequest{
	HttpHeaders getHeaders();
	
	Method getMethod();
	
	ClientHttpResponse execute() throws IOException;
}
