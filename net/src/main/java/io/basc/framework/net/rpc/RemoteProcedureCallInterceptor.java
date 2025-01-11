package io.basc.framework.net.rpc;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.core.execution.param.Args;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.client.ClientResponse;
import io.basc.framework.net.client.convert.ClientMessageConverter;
import io.basc.framework.net.rpc.factory.RemoteRequestFactory;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoteProcedureCallInterceptor implements ExecutionInterceptor {
	/**
	 * 构造请求
	 */
	@NonNull
	private final RemoteRequestFactory requestFactory;
	/**
	 * 传输协议转换
	 */
	@NonNull
	private final ClientMessageConverter messageConverter;

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		Args parameters = new Args(function.getParameterDescriptors(), args);
		ClientRequest request = requestFactory.createRequest(function, parameters);
		for (Parameter parameter : parameters.getElements()) {
			if (!messageConverter.isWriteable(parameter, request)) {
				// TODO ignore log
				continue;
			}
			messageConverter.writeTo(parameter, request, request);
		}

		ClientResponse response = request.execute();
		try {
			return messageConverter.readFrom(function.getReturnTypeDescriptor(), response);
		} finally {
			response.close();
		}
	}
}
