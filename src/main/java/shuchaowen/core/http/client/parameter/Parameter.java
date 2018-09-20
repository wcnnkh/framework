package shuchaowen.core.http.client.parameter;

import java.io.IOException;
import java.io.OutputStream;

public interface Parameter{
	void wrapper(OutputStream outputStream) throws IOException;
}
