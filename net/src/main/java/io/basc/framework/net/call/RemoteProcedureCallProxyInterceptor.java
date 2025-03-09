package io.basc.framework.net.call;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.client.ClientResponse;
import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.net.convert.uri.UriParameterConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoteProcedureCallProxyInterceptor implements ExecutionInterceptor {
	/**
	 * 构造请求
	 */
	@NonNull
	private final RemoteProcedureCallFactory remoteProcedureCallFactory;
	/**
	 * 消息转换
	 */
	@NonNull
	private final MessageConverter messageConverter;

	/**
	 * uri参数转换
	 */
	@NonNull
	private final UriParameterConverter uriParameterConverter;

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		Parameters parameters = Parameters.forTemplate(function, args);
		ClientRequest request = remoteProcedureCallFactory.createRequest(function, parameters);
		for (Parameter parameter : parameters.getElements()) {
			if (!messageConverter.isWriteable(parameter, request, null)) {
				// TODO ignore log
				continue;
			}
			messageConverter.writeTo(parameter, request, null);
		}

		ClientResponse response = request.execute();
		try {
			return messageConverter.readFrom(() -> function.getReturnTypeDescriptor(), response, null);
		} finally {
			response.close();
		}
	}
}
