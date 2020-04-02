package scw.mvc;

import java.io.IOException;
import java.io.PrintWriter;

import scw.net.message.OutputMessage;

public interface Response extends OutputMessage{
	String getRawContentType();
	
	void setContentType(String contentType);
	
	String getCharacterEncoding();

	void setCharacterEncoding(String env);

	PrintWriter getWriter() throws IOException;
	
	boolean isCommitted();
}
