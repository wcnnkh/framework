package io.basc.framework.net.rpc.factory;

import java.io.IOException;
import java.util.function.Predicate;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.client.ClientRequest;

/**
 * 构造一个rpc的请求
 * 
 * @author shuchaowen
 *
 */
public interface RemoteRequestFactory extends Predicate<Function> {

	/**
	 * 测试是否支持
	 */
	@Override
	boolean test(Function function);

	ClientRequest createRequest(Function function, Parameters parameters) throws IOException;
}
