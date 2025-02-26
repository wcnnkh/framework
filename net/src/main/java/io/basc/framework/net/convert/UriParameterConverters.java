package io.basc.framework.net.convert;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponents;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.spi.Providers;
import lombok.NonNull;

public class UriParameterConverters extends Providers<UriParameterConverter, ConversionException>
		implements UriParameterConverter {
	private static Logger logger = LogManager.getLogger(UriParameterConverters.class);

	public UriParameterConverters() {
		setServiceClass(UriParameterConverter.class);
	}

	@Override
	public boolean canConvert(@NonNull ParameterDescriptor parameterDescriptor) {
		return optional().filter((e) -> e.canConvert(parameterDescriptor)).isPresent();
	}

	@Override
	public Object readFrom(@NonNull ParameterDescriptor parameterDescriptor, @NonNull UriComponents request) {
		return optional().filter((e) -> e.canConvert(parameterDescriptor)).apply((converter) -> {
			if (converter == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support read descriptor={}, request={}", parameterDescriptor, request);
				}
				return null;
			}

			if (logger.isTraceEnabled()) {
				logger.trace("{} read descriptor={}, request={}", converter, parameterDescriptor, request);
			}
			return converter.readFrom(parameterDescriptor, request);
		});
	}

	@Override
	public void writeTo(@NonNull Parameter parameter, @NonNull UriComponentsBuilder response) {
		optional().filter((e) -> e.canConvert(parameter)).map((e) -> {
			if (e == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support wirte parameter={}, response={}", parameter, response);
				}
			}
			return e;
		}).ifPresent((converter) -> {
			if (logger.isTraceEnabled()) {
				logger.trace("{} write parameter={}, response={}", converter, parameter, response);
			}
			converter.writeTo(parameter, response);
		});
	}
}
