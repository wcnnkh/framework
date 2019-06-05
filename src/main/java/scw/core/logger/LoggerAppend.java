package scw.core.logger;

import java.io.IOException;

public interface LoggerAppend {
	void appendLogger(Appendable appendable) throws IOException;
}
