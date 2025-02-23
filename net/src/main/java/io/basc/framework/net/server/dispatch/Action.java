package io.basc.framework.net.server.dispatch;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.RequestPatternCapable;
import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.convert.ServerMessageConverter;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Action implements Server, ExecutionInterceptor, RequestPatternCapable {
	@NonNull
	private final Function function;
	@NonNull
	private final RequestPattern requestPattern;
	@NonNull
	private ServerMessageConverter messageConverter;
	private ErrorHandler errorHandler;
	private ExecutionInterceptor executionInterceptor;

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		if (executionInterceptor == null) {
			return function.execute(args);
		} else {
			return executionInterceptor.intercept(function, args);
		}
	}

	protected Object getArg(ServerRequest request, Properties requestPatternProperties,
			ParameterDescriptor parameterDescriptor) throws IOException {
		// 优先匹配额外参数
		Property property = requestPatternProperties.get(parameterDescriptor.getName());
		if (property != null && property.isReadable()) {
			return property.getAsObject(parameterDescriptor.getRequiredTypeDescriptor());
		}
		return messageConverter.readFrom(parameterDescriptor, request);
	}

	private Object[] getArgs(ServerRequest request) throws IOException {
		// 额外参数
		Properties requestPatternProperties = requestPattern.apply(request);
		ParameterDescriptor[] paraemterDescriptors = function.getParameterDescriptors()
				.toArray(new ParameterDescriptor[0]);
		Object[] args = new Object[paraemterDescriptors.length];
		for (int i = 0; i < paraemterDescriptors.length; i++) {
			args[i] = getArg(request, requestPatternProperties, paraemterDescriptors[i]);
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

		Source responseValue = Source.of(rtn, function.getReturnTypeDescriptor());
		for (MediaType mimeType : requestPattern.getProduces()) {
			if (messageConverter.isWriteable(responseValue, mimeType)) {
				messageConverter.writeTo(responseValue, request, response);
				return;
			}
		}
	}
}
