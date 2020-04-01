package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;

import scw.net.message.InputMessage;

public interface Request extends InputMessage {
	String getControllerPath();
	
	String getRawContentType();

	String getContextPath();

	String getCharacterEncoding();

	void setCharacterEncoding(String env);

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
