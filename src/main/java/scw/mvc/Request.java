package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public interface Request {
	String getContentType();
	
	String getCharacterEncoding();

    void setCharacterEncoding(String env);
	
	InputStream getInputStream() throws IOException;
	
	BufferedReader getReader() throws IOException;
}
