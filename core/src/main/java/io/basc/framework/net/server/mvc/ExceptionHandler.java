package io.basc.framework.net.server.mvc;

import java.io.IOException;

import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;

/**
 * 异常处理
 * 
 * @author shuchaowen
 *
 */
public interface ExceptionHandler {
	void doError(ServerRequest request, ServerResponse response, Action action, Throwable error) throws IOException;
}
