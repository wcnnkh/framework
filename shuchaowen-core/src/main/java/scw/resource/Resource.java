package scw.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import scw.lang.NotFoundException;

public interface Resource {
	boolean isExist();

	InputStream getInputStream() throws IOException, NotFoundException;

	URI getURI() throws IOException;
}
