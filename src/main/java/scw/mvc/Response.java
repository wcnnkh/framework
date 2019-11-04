package scw.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface Response {
	void setContentType(String contentType);

	String getContentType();

	String getCharacterEncoding();

	void setCharacterEncoding(String env);

	OutputStream getOutputStream() throws IOException;

	PrintWriter getWriter() throws IOException;
}
