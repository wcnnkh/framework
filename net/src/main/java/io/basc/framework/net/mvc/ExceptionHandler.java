package io.basc.framework.net.mvc;

import java.io.IOException;

import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;

/**
 * 异常处理
 * 
 * @author shuchaowen
 *
 */
public interface ExceptionHandler {
	void doError(ServerRequest request, ServerResponse response, Server server, Throwable error) throws IOException;
}
