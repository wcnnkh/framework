package shuchaowen.connection;

import java.io.IOException;

public interface RequestEntity {
	
	void write(Request request) throws IOException;
}
