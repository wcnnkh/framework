package scw.io.resource;

import java.io.IOException;
import java.net.URI;

public interface Resource {
	boolean isExist();

	URI getURI() throws IOException;
}
