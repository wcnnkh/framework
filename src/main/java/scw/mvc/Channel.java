package scw.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.logger.LogService;

public interface Channel extends LogService {
	long getCreateTime();

	Object getParameter(ParameterDefinition parameterDefinition);

	void write(Object obj) throws Throwable;

	OutputStream getOutputStream() throws IOException;

	InputStream getInputStream() throws IOException;
}
