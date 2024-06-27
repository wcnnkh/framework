package io.basc.framework.net.server.dispatch;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Service;

/**
 * 调度器
 * 
 * @author shuchaowen
 *
 */
public interface Dispatcher {
	@Nullable
	Service dispatch(ServerRequest request);
}
