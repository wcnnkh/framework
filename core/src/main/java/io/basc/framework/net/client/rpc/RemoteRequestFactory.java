package io.basc.framework.net.client.rpc;

import java.io.IOException;

import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.util.element.Elements;

/**
 * 构造一个rpc的请求
 * 
 * @author shuchaowen
 *
 */
public interface RemoteRequestFactory extends ExecutionInterceptor {

	@Override
	ClientRequest intercept(Function function, Elements<? extends Object> args) throws IOException;
}
