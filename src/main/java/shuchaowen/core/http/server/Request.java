package shuchaowen.core.http.server;

import java.io.IOException;
import java.io.InputStream;

public interface Request {
	InputStream getInputStream() throws IOException;
	
	<T> T getParameter(Class<T> type, String name) throws Throwable;
	
	String getHeader(String name);
	
	String getContentType();
	
	String getMethod();
	
	String getPath();
	
	void setAttribute(String name, Object value);
	
	Object getAttribute(String name);
}
