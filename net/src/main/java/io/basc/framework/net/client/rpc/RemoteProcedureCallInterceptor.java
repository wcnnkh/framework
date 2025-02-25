package io.basc.framework.net.client.rpc;

import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.client.ClientResponse;
import io.basc.framework.net.client.convert.ClientMessageConverter;
import io.basc.framework.net.client.rpc.factory.RemoteRequestFactory;
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
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		Parameters parameters = Parameters.forTemplate(function, args);
		ClientRequest request = requestFactory.createRequest(function, parameters);
		for (Parameter parameter : parameters.getElements()) {
			if (!messageConverter.isWriteable(parameter, request.getContentType())) {
				// TODO ignore log
				continue;
			}
			messageConverter.writeTo(parameter, null, request, request);
		}

		ClientResponse response = request.execute();
		try {
			return messageConverter.readFrom(AccessDescriptor.of(function.getReturnTypeDescriptor()), null, response);
		} finally {
			response.close();
		}
	}
}
