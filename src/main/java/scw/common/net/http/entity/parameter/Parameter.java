package scw.common.net.http.entity.parameter;

import java.io.IOException;
import java.io.OutputStream;

public interface Parameter {
	void write(OutputStream out) throws IOException;
}
