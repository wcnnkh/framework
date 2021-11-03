package io.basc.framework.web.message;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

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
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(parameterDescriptor)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public boolean isAccept(HttpRequest request, ParameterDescriptor parameterDescriptor) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(request, parameterDescriptor)) {
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
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(parameterDescriptor)) {
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
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(parameterDescriptor)) {
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
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(parameterDescriptor)) {
					return converter.write(builder, parameterDescriptor, parameter);
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return builder;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(message, typeDescriptor)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor, Object body) {
		for (WebMessageConverter converter : this) {
			if (NESTED.exists(converter)) {
				continue;
			}

			NESTED.set(converter);
			try {
				if (converter.isAccept(message, typeDescriptor, body)) {
					return true;
				}
			} finally {
				NESTED.remove(converter);
			}
		}
		return false;
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
				if (converter.isAccept(response, typeDescriptor)) {
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
				if (converter.isAccept(response, typeDescriptor, body)) {
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
