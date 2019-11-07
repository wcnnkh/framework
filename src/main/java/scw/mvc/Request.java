package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public interface Request{
	String getContentType();
	
	String getCharacterEncoding();

    void setCharacterEncoding(String env);
	
	InputStream getInputStream() throws IOException;
	
	BufferedReader getReader() throws IOException;
	
	/**
	 * Returns the Internet Protocol (IP) address of the client or last proxy
	 * that sent the request. For HTTP servlets, same as the value of the CGI
	 * variable <code>REMOTE_ADDR</code>.
	 *
	 * @return a <code>String</code> containing the IP address of the client
	 *         that sent the request
	 */
	String getRemoteAddr();
}
