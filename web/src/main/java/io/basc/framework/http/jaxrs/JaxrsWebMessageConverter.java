package io.basc.framework.http.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Method;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.aop.Aop;
import io.basc.framework.core.execution.aop.MethodExecutionInterceptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;
import lombok.NonNull;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxrsWebMessageConverter implements WebMessageConverter, Configurable {
	private MessageBodyReaders messageBodyReaders = new MessageBodyReaders<>();
	private MessageBodyWriters messageBodyWriters = new MessageBodyWriters<>();

	{
		messageBodyReaders.setServiceClass(MessageBodyReader.class);
		messageBodyWriters.setServiceClass(MessageBodyWriter.class);
	}

	@Override
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		messageBodyReaders.doConfigure(discovery);
		messageBodyWriters.doConfigure(discovery);
		return Receipt.SUCCESS;
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		MediaType mediaType = JaxrsUtils.convertMediaType(message.getContentType());
		Annotation[] annotations = descriptor.getAnnotations();
		return messageBodyReaders.isReadable(descriptor.getType(), descriptor.getResolvableType().getType(),
				annotations, mediaType);
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		MediaType mediaType = JaxrsUtils.convertMediaType(message.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		return messageBodyWriters.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				annotations, mediaType);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		MediaType mediaType = JaxrsUtils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getTypeDescriptor().getAnnotations();
		MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(request.getHeaders());

		if (messageBodyReaders.isReadable(parameterDescriptor.getTypeDescriptor().getType(),
				parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType)) {
			return messageBodyReaders.readFrom(parameterDescriptor.getTypeDescriptor().getType(),
					parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType,
					headerMap, request.getInputStream());
		}
		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		MediaType mediaType = JaxrsUtils.convertMediaType(response.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		if (messageBodyWriters.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				annotations, mediaType)) {
			MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(response.getHeaders());
			// 代理response output, 因为一些实现会在调用getOutputStream后无法再设置Headers
			OutputStream proxyOutput = getProxyOutputStream(null, headerMap);
			messageBodyWriters.writeTo(body, typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
					annotations, mediaType, headerMap, proxyOutput);
			return;
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
		MediaType mediaType = JaxrsUtils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getTypeDescriptor().getAnnotations();
		if (messageBodyWriters.isWriteable(parameterDescriptor.getTypeDescriptor().getType(),
				parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType)) {
			MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(request.getHeaders());
			OutputStream proxyOutput = getProxyOutputStream(request, headerMap);
			messageBodyWriters.writeTo(parameter, parameterDescriptor.getTypeDescriptor().getType(),
					parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType,
					headerMap, proxyOutput);
		}
		return request;
	}

	protected OutputStream getProxyOutputStream(ClientHttpRequest request, MultivaluedMap<String, String> headerMap) {
		return (OutputStream) Aop.global()
				.getProxy(OutputStream.class, null, new OutputStreamMethodExecutionInterceptor(request, headerMap))
				.execute();
	}

	/**
	 * 代理output, 因为一些实现会在调用getOutputStream后无法再设置Headers
	 * 
	 * @author wcnnkh
	 *
	 */
	private static class OutputStreamMethodExecutionInterceptor implements MethodExecutionInterceptor {
		private final OutputMessage outputMessage;
		private final MultivaluedMap<String, String> headerMap;
		private volatile boolean headerTag;
		private volatile OutputStream outputStream;

		public OutputStreamMethodExecutionInterceptor(OutputMessage outputMessage,
				MultivaluedMap<String, String> headerMap) {
			this.outputMessage = outputMessage;
			this.headerMap = headerMap;
		}

		@Override
		public Object intercept(@NonNull Method executor, @NonNull Object... args) throws Throwable {
			if (!outputMessage.getHeaders().isReadyOnly()) {
				if (!headerTag) {
					synchronized (this) {
						if (!headerTag) {
							headerTag = true;
							outputMessage.getHeaders().putAll(headerMap);
						}
					}
				}
			}

			if (outputStream == null) {
				synchronized (this) {
					if (outputStream == null) {
						outputStream = outputMessage.getOutputStream();
					}
				}
			}
			return executor.execute(outputStream, args);
		}
	}
}
