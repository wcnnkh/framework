package shuchaowen.core.http.server;

import java.io.IOException;
import java.io.OutputStream;

public interface Response {
	OutputStream getOutputStream() throws IOException;

	void write(Object data) throws IOException;
	
	String getHeader(String name);
	
	void setHeader(String name, String value);
	
	String getContentType();
	
	void setContentType(String contentType);
}
