package scw.web.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.convert.TypeDescriptor;
import scw.core.OrderComparator;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public class WebMessageConverters implements WebMessageConverter, WebMessageConverterAware {
	private volatile List<WebMessageConverter> converters;
	private WebMessageConverter parentMessageConverter;
	private WebMessageConverter awareMessageConverter = this;

	public WebMessageConverters() {
	}

	@Override
	public void setWebMessageConverter(WebMessageConverter messageConverter) {
		this.awareMessageConverter = messageConverter;
	}

	public WebMessageConverters(WebMessageConverter parentMessageConverter) {
		this.parentMessageConverter = parentMessageConverter;
	}

	public void addMessageConverter(WebMessageConverter converter) {
		synchronized (this) {
			if (converters == null) {
				converters = new ArrayList<WebMessageConverter>(8);
			}

			if (converter instanceof WebMessageConverterAware) {
				((WebMessageConverterAware) converter).setWebMessageConverter(awareMessageConverter);
			}

			converters.add(converter);
			Collections.sort(converters, OrderComparator.INSTANCE.reversed());
		}
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		for (WebMessageConverter converter : converters) {
			if (converter.canRead(parameterDescriptor, request)) {
				return true;
			}
		}
		return (parentMessageConverter != null && parentMessageConverter.canRead(parameterDescriptor, request));
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		for (WebMessageConverter converter : converters) {
			if (converter.canRead(parameterDescriptor, request)) {
				return converter.read(parameterDescriptor, request);
			}
		}

		if (parentMessageConverter != null && parentMessageConverter.canRead(parameterDescriptor, request)) {
			return parentMessageConverter.read(parameterDescriptor, request);
		}

		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body) {
		if (body != null && body instanceof WebMessageWriter) {
			return true;
		}

		for (WebMessageConverter converter : converters) {
			if (converter.canWrite(type, body)) {
				return true;
			}
		}
		return (parentMessageConverter != null && parentMessageConverter.canWrite(type, body));
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		if (body != null && body instanceof WebMessageWriter) {
			((WebMessageWriter) body).write(request, response);
			return;
		}

		for (WebMessageConverter converter : converters) {
			if (converter.canWrite(type, body)) {
				converter.write(type, body, request, response);
				return;
			}
		}

		if (parentMessageConverter != null && parentMessageConverter.canWrite(type, body)) {
			parentMessageConverter.write(type, body, request, response);
			return;
		}

		throw new WebMessagelConverterException(type, body, request, null);
	}

}
