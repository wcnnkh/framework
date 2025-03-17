package run.soeasy.framework.net.server;

import java.io.IOException;

/**
 * 异常处理
 * 
 * @author shuchaowen
 *
 */
public interface ErrorHandler {
	void doError(ServerRequest request, ServerResponse response, Service server, Throwable error) throws IOException;
}
