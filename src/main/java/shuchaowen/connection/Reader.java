package shuchaowen.connection;

import java.io.IOException;
import java.io.InputStream;

public interface Reader<T>{
	T reader(InputStream inputStream) throws IOException;
}
