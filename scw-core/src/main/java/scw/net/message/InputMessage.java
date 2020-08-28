package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

public interface InputMessage extends Message {
	InputStream getBody() throws IOException;
}
