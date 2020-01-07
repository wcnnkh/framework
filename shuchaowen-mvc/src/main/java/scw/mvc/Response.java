package scw.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import scw.util.MimeType;

public interface Response {
	void setContentType(String contentType);
	
	void setMimeType(MimeType mimeType);

	String getContentType();

	String getCharacterEncoding();

	void setCharacterEncoding(String env);

	OutputStream getOutputStream() throws IOException;

	PrintWriter getWriter() throws IOException;
}
