package scw.mvc2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import scw.core.attribute.Attributes;

public interface Request extends Attributes<Object>{
	String getContentType();
	
	String getCharacterEncoding();

    void setCharacterEncoding(String env);
	
	InputStream getInputStream() throws IOException;
	
	BufferedReader getReader() throws IOException;
}
