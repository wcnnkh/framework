package io.basc.framework.web.message;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class WebMessageConverters extends ConfigurableServices<WebMessageConverter>
		implements WebMessageConverter {
	private static final LinkedThreadLocal<WebMessageConverter> NESTED = new LinkedThreadLocal<WebMessageConverter>(
			WebMessageConverters.class.getName());

	public WebMessageConverters() {
		super(WebMessageConverter.class);
	}

	protected void aware(WebMessageConverter converter) {
		if (converter instanceof WebMessageConverterAware) {
			((WebMessageConverterAware) converter).setWebMessageConverter(this);
		}
		super.aware(converter);
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canRead(parameterDescriptor, request)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canRead(parameterDescriptor, request)) {
					return converter.read(parameterDescriptor, request);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		if (body != null && body instanceof WebMessageWriter) {
			return true;
		}

		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(type, body, request, response)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		if (body != null && body instanceof WebMessageWriter) {
			((WebMessageWriter) body).write(request, response);
			return;
		}

		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(type, body, request, response)) {
					converter.write(type, body, request, response);
					return;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		throw new WebMessagelConverterException(type, body, request, null);
	}

}
