package run.soeasy.framework.http.client;

import java.io.IOException;

public interface ClientHttpRequestExecutor {
	ClientHttpResponse execute(ClientHttpRequest request) throws IOException;
}
