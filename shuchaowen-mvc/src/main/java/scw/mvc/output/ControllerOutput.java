package scw.mvc.output;

import java.io.IOException;

import scw.mvc.HttpChannel;

public interface ControllerOutput {
	boolean canWrite(HttpChannel httpChannel, Object body);

	void write(HttpChannel httpChannel, Object body) throws IOException;
}
