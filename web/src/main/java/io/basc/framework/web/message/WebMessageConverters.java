package io.basc.framework.web.message;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

public class WebMessageConverters extends ConfigurableServices<WebMessageConverter> implements WebMessageConverter {
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
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canRead(message, descriptor)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(message, typeDescriptor, value)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(typeDescriptor, parameter)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		TypeDescriptor typeDescriptor = new TypeDescriptor(parameterDescriptor);
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canRead(request, typeDescriptor)) {
					return converter.read(request, parameterDescriptor);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		TypeDescriptor typeDescriptor = new TypeDescriptor(parameterDescriptor);
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(request, typeDescriptor, parameter)) {
					return converter.write(request, parameterDescriptor, parameter);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return request;
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		TypeDescriptor typeDescriptor = new TypeDescriptor(parameterDescriptor);
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(typeDescriptor, parameter)) {
					return converter.write(builder, parameterDescriptor, parameter);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return builder;
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canRead(response, typeDescriptor)) {
					return converter.read(response, typeDescriptor);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		throw new WebMessagelConverterException(typeDescriptor.toString());
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.canWrite(response, typeDescriptor, body)) {
					converter.write(request, response, typeDescriptor, body);
					return;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		throw new WebMessagelConverterException(typeDescriptor, body, request, null);
	}
}
