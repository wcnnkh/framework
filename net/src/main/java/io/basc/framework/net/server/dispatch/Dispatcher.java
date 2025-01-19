package io.basc.framework.net.server.dispatch;

import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Server;

/**
 * 调度器
 * 
 * @author shuchaowen
 *
 */
public interface Dispatcher {
	Server dispatch(ServerRequest request);
}
