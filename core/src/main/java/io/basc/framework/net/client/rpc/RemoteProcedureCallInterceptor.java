package io.basc.framework.net.client.rpc;

import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.SimpleParameter;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.client.ClientResponse;
import io.basc.framework.net.client.convert.ClientMessageConverter;
import io.basc.framework.util.element.Elements;
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
		ClientRequest request = requestFactory.intercept(function, args);
		Elements<Parameter> parameters = function.getParameterDescriptors().parallel(args).map((e) -> {
			SimpleParameter parameter = new SimpleParameter(e.getLeftValue());
			parameter.setValue(e.getRightValue());
			return parameter;
		});

		for (Parameter parameter : parameters) {
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
