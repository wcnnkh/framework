package scw.web.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.core.OrderComparator;
import scw.core.parameter.ParameterDescriptor;
import scw.lang.LinkedThreadLocal;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public class WebMessageConverters implements WebMessageConverter, WebMessageConverterAware,
		Iterable<WebMessageConverter>, ConversionServiceAware {
	private static final LinkedThreadLocal<WebMessageConverter> NESTED = new LinkedThreadLocal<WebMessageConverter>(
			WebMessageConverters.class.getName());

	private volatile List<WebMessageConverter> converters;
	private WebMessageConverter parentMessageConverter;
	private ConversionService conversionService;
	private WebMessageConverter awareMessageConverter = this;

	public WebMessageConverters() {
	}

	public WebMessageConverters(WebMessageConverter parentMessageConverter) {
		this.parentMessageConverter = parentMessageConverter;
		if (parentMessageConverter instanceof WebMessageConverters) {
			this.conversionService = ((WebMessageConverters) parentMessageConverter).conversionService;
		}
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public void setWebMessageConverter(WebMessageConverter messageConverter) {
		this.awareMessageConverter = messageConverter;
	}

	protected void aware(WebMessageConverter converter) {
		if (converter instanceof WebMessageConverterAware) {
			((WebMessageConverterAware) converter).setWebMessageConverter(awareMessageConverter);
		}

		if (converter instanceof ConversionServiceAware) {
			((ConversionServiceAware) converter).setConversionService(conversionService);
		}
	}

	public void addMessageConverter(WebMessageConverter converter) {
		synchronized (this) {
			if (converters == null) {
				converters = new ArrayList<WebMessageConverter>(8);
			}

			aware(converter);

			converters.add(converter);
			Collections.sort(converters, OrderComparator.INSTANCE.reversed());
		}
	}

	@Override
	public Iterator<WebMessageConverter> iterator() {
		if (converters == null) {
			return Collections.emptyIterator();
		}

		return converters.iterator();
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
		return (parentMessageConverter != null && parentMessageConverter.canRead(parameterDescriptor, request));
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

		if (parentMessageConverter != null && parentMessageConverter.canRead(parameterDescriptor, request)) {
			return parentMessageConverter.read(parameterDescriptor, request);
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
		return (parentMessageConverter != null && parentMessageConverter.canWrite(type, body, request, response));
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

		if (parentMessageConverter != null && parentMessageConverter.canWrite(type, body, request, response)) {
			parentMessageConverter.write(type, body, request, response);
			return;
		}

		throw new WebMessagelConverterException(type, body, request, null);
	}

}
