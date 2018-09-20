package shuchaowen.core.http.client.parameter.file;

import java.io.IOException;
import java.io.InputStream;

public interface File {
	public String contentType();
	
	public long length();
	
	public String fileName();
	
	public String name();
	
	public InputStream inputStream() throws IOException;
}
