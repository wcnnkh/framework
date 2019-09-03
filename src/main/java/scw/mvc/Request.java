package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public interface Request {
	String getCharacterEncoding();

    void setCharacterEncoding(String env)
            throws java.io.UnsupportedEncodingException;
	
	InputStream getInputStream() throws IOException;
	
	BufferedReader getReader() throws IOException;
}
