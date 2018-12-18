package scw.servlet.upload;

import java.io.IOException;

import scw.servlet.Request;

public interface Upload {
	void execute(Request request) throws IOException;
}
