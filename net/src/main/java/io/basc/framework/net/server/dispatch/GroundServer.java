package io.basc.framework.net.server.dispatch;

import java.io.IOException;

import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;

/**
 * 兜底服务
 * 
 * @author alisa
 *
 */
public interface GroundServer extends Server {
	@Override
	void service(ServerRequest request, ServerResponse response) throws IOException, ServerException;
}
