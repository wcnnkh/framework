package scw.mvc;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

import scw.net.message.OutputMessage;

public interface ServerResponse extends OutputMessage, Flushable{
	String getRawContentType();
	
	void setContentType(String contentType);
	
	String getCharacterEncoding();

	PrintWriter getWriter() throws IOException;
	
	boolean isCommitted();
}
