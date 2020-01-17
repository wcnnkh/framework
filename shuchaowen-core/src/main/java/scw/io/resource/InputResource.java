package scw.io.resource;

import java.io.IOException;
import java.io.InputStream;

import scw.lang.NotFoundException;

public interface InputResource extends Resource{
	InputStream getInputStream() throws IOException, NotFoundException;
}
