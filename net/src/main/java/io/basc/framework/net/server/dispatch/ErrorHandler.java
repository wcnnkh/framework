package io.basc.framework.net.server.dispatch;

import java.io.IOException;

import io.basc.framework.net.server.Service;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;

/**
 * 异常处理
 * 
 * @author shuchaowen
 *
 */
public interface ErrorHandler {
	void doError(ServerRequest request, ServerResponse response, Service server, Throwable error) throws IOException;
}
