package io.basc.framework.web.jaxrs;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class Jaxrs2WebMessageConverter implements WebMessageConverter, Configurable {
	private final ConfigurableServices<MessageBodyReader> messageBodyReaders = new ConfigurableServices<>(
			MessageBodyReader.class);
	private final ConfigurableServices<MessageBodyWriter> messageBodyWriters = new ConfigurableServices<>(
			MessageBodyWriter.class);

	public ConfigurableServices<MessageBodyReader> getMessageBodyReaders() {
		return messageBodyReaders;
	}

	public ConfigurableServices<MessageBodyWriter> getMessageBodyWriters() {
		return messageBodyWriters;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageBodyReaders.configure(serviceLoaderFactory);
		messageBodyWriters.configure(serviceLoaderFactory);
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		MediaType mediaType = Jaxrs2Utils.convertMediaType(message.getContentType());
		Annotation[] annotations = descriptor.getAnnotations();
		for (MessageBodyReader messageBodyReader : messageBodyReaders) {
			if (messageBodyReader.isReadable(descriptor.getType(), descriptor.getResolvableType().getType(),
					annotations, mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		MediaType mediaType = Jaxrs2Utils.convertMediaType(message.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters) {
			if (messageBodyWriter.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
					annotations, mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = Jaxrs2Utils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getAnnotations();
		MultivaluedMap<String, String> headerMap = Jaxrs2Utils.convertHeaders(request.getHeaders());
		for (MessageBodyReader messageBodyReader : messageBodyReaders) {
			if (messageBodyReader.isReadable(parameterDescriptor.getType(), parameterDescriptor.getGenericType(),
					annotations, mediaType)) {
				return messageBodyReader.readFrom(parameterDescriptor.getType(), parameterDescriptor.getGenericType(),
						annotations, mediaType, headerMap, request.getInputStream());
			}
		}
		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		MediaType mediaType = Jaxrs2Utils.convertMediaType(response.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters) {
			if (messageBodyWriter.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
					annotations, mediaType)) {
				MultivaluedMap<String, String> headerMap = Jaxrs2Utils.convertHeaders(response.getHeaders());
				// 代理response output, 因为一些实现会在调用getOutputStream后无法再设置Headers
				OutputStream proxyOutput = (OutputStream) ProxyUtils.getFactory()
						.getProxy(OutputStream.class, null, (invoker, args) -> {
							if (!response.getHeaders().isReadyOnly()) {
								response.getHeaders().putAll(headerMap);
							}
							return invoker.getMethod().invoke(response.getOutputStream(), args);
						}).create();
				messageBodyWriter.writeTo(body, typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
						annotations, mediaType, headerMap, proxyOutput);
				return;
			}
		}
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return null;
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = Jaxrs2Utils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters) {
			if (messageBodyWriter.isWriteable(parameterDescriptor.getType(), parameterDescriptor.getType(), annotations,
					mediaType)) {
				MultivaluedMap<String, String> headerMap = Jaxrs2Utils.convertHeaders(request.getHeaders());
				// 代理response output, 因为一些实现会在调用getOutputStream后无法再设置Headers
				OutputStream proxyOutput = (OutputStream) ProxyUtils.getFactory()
						.getProxy(OutputStream.class, null, (invoker, args) -> {
							if (!request.getHeaders().isReadyOnly()) {
								request.getHeaders().putAll(headerMap);
							}
							return invoker.getMethod().invoke(request.getOutputStream(), args);
						}).create();
				messageBodyWriter.writeTo(parameter, parameterDescriptor.getType(), parameterDescriptor.getType(),
						annotations, mediaType, headerMap, proxyOutput);
				break;
			}
		}
		return request;
	}
}
