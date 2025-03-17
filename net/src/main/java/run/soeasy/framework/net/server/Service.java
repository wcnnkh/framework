package run.soeasy.framework.net.server;

import java.io.IOException;

public interface Service {
	void service(ServerRequest request, ServerResponse response) throws IOException, ServerException;
}
