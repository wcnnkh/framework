package io.basc.framework.net.server.mvc;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Service;
import io.basc.framework.net.server.convert.ServerMessageConverter;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Action implements Service, ExecutionInterceptor {
	@NonNull
	private final Function function;
	@NonNull
	private final Controller controller;
	@NonNull
	private ServerMessageConverter messageConverter;
	private ExceptionHandler errorHandler;
	private ExecutionInterceptor executionInterceptor;

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if (executionInterceptor == null) {
			return function.execute(args);
		} else {
			return executionInterceptor.intercept(function, args);
		}
	}

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException {
		ParameterDescriptor[] paraemterDescriptors = function.getParameterDescriptors()
				.toArray(new ParameterDescriptor[0]);
		Object[] args = new Object[paraemterDescriptors.length];
		for (int i = 0; i < paraemterDescriptors.length; i++) {
			args[i] = messageConverter.readFrom(paraemterDescriptors[i], request, request);
		}

		Object rtn;
		try {
			rtn = intercept(function, Elements.forArray(args));
		} catch (Throwable e) {
			if (errorHandler == null) {
				throw new ServerException(e);
			}
			errorHandler.doError(request, response, this, e);
			return;
		}

		TypeDescriptor rtnTypeDescriptor = function.getReturnTypeDescriptor();
		Value responseValue = Value.of(rtn, function.getReturnTypeDescriptor());
		for (MimeType mimeType : controller.getProduces()) {
			if (messageConverter.isWriteable(rtnTypeDescriptor, mimeType)) {
				messageConverter.writeTo(responseValue, mimeType, response);
				return;
			}
		}
		// 兜底的message converter
		messageConverter.writeTo(responseValue, null, response);
	}
}
