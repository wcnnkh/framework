package run.soeasy.framework.io.watch;

import java.io.IOException;

public interface Variable {
	long lastModified() throws IOException;
}
