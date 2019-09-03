package scw.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface Response {
	
    String getCharacterEncoding();

    void setCharacterEncoding(String env)
            throws java.io.UnsupportedEncodingException;
	
	OutputStream getOutputStream() throws IOException;

	PrintWriter getWriter() throws IOException;
}
