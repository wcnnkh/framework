package io.basc.framework.net.call;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.RequestPatternCapable;
import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.net.server.ErrorHandler;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Service;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Action implements Service, RequestPatternCapable {
	@NonNull
	private final Function function;
	@NonNull
	private final RequestPattern requestPattern;
	@NonNull
	private MessageConverter messageConverter;
	private ErrorHandler errorHandler;

	protected Object getArg(ParameterDescriptor parameterDescriptor, ServerRequest request, ServerResponse response,
			Properties requestPatternProperties) throws IOException {
		// 优先匹配额外参数
		Property property = requestPatternProperties.get(parameterDescriptor.getName());
		if (property != null && property.isReadable()) {
			return property.getAsObject(parameterDescriptor.getRequiredTypeDescriptor());
		}

		for (MediaType mediaType : getRequestPattern().getConsumes()) {
			if (messageConverter.isReadable(parameterDescriptor, request, mediaType)) {
				return messageConverter.readFrom(parameterDescriptor, request, mediaType);
			}
		}
		return messageConverter.readFrom(parameterDescriptor, request, request.getContentType());
	}

	private Object[] getArgs(ServerRequest request, ServerResponse response) throws IOException {
		// 额外参数
		Properties requestPatternProperties = requestPattern.apply(request);
		ParameterDescriptor[] paraemterDescriptors = function.getParameterDescriptors()
				.toArray(new ParameterDescriptor[0]);
		Object[] args = new Object[paraemterDescriptors.length];
		for (int i = 0; i < paraemterDescriptors.length; i++) {
			args[i] = getArg(paraemterDescriptors[i], request, response, requestPatternProperties);
		}
		return args;
	}

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException {
		Object[] args = getArgs(request, response);
		Object rtn;
		try {
			rtn = function.execute(args);
		} catch (Throwable e) {
			if (errorHandler == null) {
				throw new ServerException(e);
			}
			errorHandler.doError(request, response, this, e);
			return;
		}

		Source responseValue = Source.of(rtn, function.getReturnTypeDescriptor());
		if (response.getContentType() == null) {
			for (MediaType mimeType : requestPattern.getProduces()) {
				if (messageConverter.isWriteable(responseValue, response, mimeType)) {
					messageConverter.writeTo(responseValue, response, mimeType);
					return;
				}
			}
		} else {
			messageConverter.writeTo(responseValue, response, response.getContentType());
		}
	}
}
