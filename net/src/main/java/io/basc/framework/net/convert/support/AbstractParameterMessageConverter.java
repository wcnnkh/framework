package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.Response;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractParameterMessageConverter extends AbstractMessageConverter {
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

	public abstract Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage request,
			@NonNull Response response) throws IOException;

	@Override
	protected final Object doRead(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage request, @NonNull Response response) throws IOException {
		if (targetDescriptor instanceof ParameterDescriptor) {
			return doRead((ParameterDescriptor) targetDescriptor, request, response);
		}
		if (targetDescriptor.isRequired()) {
			throw new UnsupportedOperationException("required descriptor:" + targetDescriptor);
		}
		return null;
	}

	public abstract void doWrite(@NonNull Parameter parameter, @NonNull Request request,
			@NonNull OutputMessage response) throws IOException;

	@Override
	protected final void doWrite(Source source, MediaType contentType, Request request, OutputMessage response)
			throws IOException {
		if (source instanceof Parameter) {
			doWrite((Parameter) source, request, response);
		}
	}

	public abstract boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message request);

	@Override
	public final boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message request) {
		if (targetDescriptor instanceof ParameterDescriptor) {
			return isReadable((ParameterDescriptor) targetDescriptor, request);
		}
		return false;
	}

	public abstract boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message response);

	@Override
	public final boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message response) {
		if (sourceDescriptor instanceof ParameterDescriptor) {
			return isWriteable((ParameterDescriptor) sourceDescriptor, response);
		}
		return false;
	}
}
