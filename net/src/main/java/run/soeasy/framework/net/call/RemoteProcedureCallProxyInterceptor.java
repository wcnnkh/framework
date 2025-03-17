package run.soeasy.framework.net.call;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.Parameter;
import run.soeasy.framework.core.execution.Parameters;
import run.soeasy.framework.core.execution.aop.ExecutionInterceptor;
import run.soeasy.framework.net.client.ClientRequest;
import run.soeasy.framework.net.client.ClientResponse;
import run.soeasy.framework.net.convert.MessageConverter;
import run.soeasy.framework.net.convert.uri.UriParameterConverter;

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
