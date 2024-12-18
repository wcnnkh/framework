package io.basc.framework.net.mvc;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.RequestPatternCapable;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Service;
import io.basc.framework.net.server.convert.ServerMessageConverter;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Action implements Service, ExecutionInterceptor, RequestPatternCapable {
	@NonNull
	private final Function function;
	@NonNull
	private final RequestPattern requestPattern;
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

	protected Object getArg(ServerRequest request, Parameters requestPatternParameters,
			ParameterDescriptor parameterDescriptor) throws IOException {
		// 优先匹配额外参数
		Elements<Parameter> elements = requestPatternParameters.getElements(parameterDescriptor);
		for (Parameter parameter : elements) {
			if (parameter.test(parameterDescriptor)) {
				return parameter.getAsObject(parameterDescriptor.getTypeDescriptor());
			}
		}
		return messageConverter.readFrom(parameterDescriptor, request, request);
	}

	private Object[] getArgs(ServerRequest request) throws IOException {
		// 额外参数
		Parameters requestPatternParameters = requestPattern.apply(request);
		ParameterDescriptor[] paraemterDescriptors = function.getParameterDescriptors()
				.toArray(new ParameterDescriptor[0]);
		Object[] args = new Object[paraemterDescriptors.length];
		for (int i = 0; i < paraemterDescriptors.length; i++) {
			args[i] = getArg(request, requestPatternParameters, paraemterDescriptors[i]);
		}
		return args;
	}

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException {
		Object[] args = getArgs(request);
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
		Any responseValue = Any.of(rtn, function.getReturnTypeDescriptor());
		for (MimeType mimeType : requestPattern.getProduces()) {
			if (messageConverter.isWriteable(rtnTypeDescriptor, mimeType)) {
				messageConverter.writeTo(responseValue, mimeType, response);
				return;
			}
		}
		// 兜底的message converter
		messageConverter.writeTo(responseValue, null, response);
	}
}
