package io.basc.framework.context.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.reflect.ReflectionMethodExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxrsWebMessageConverter implements WebMessageConverter, Configurable {
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

	private boolean configured;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageBodyReaders.configure(serviceLoaderFactory);
		messageBodyWriters.configure(serviceLoaderFactory);
		configured = true;
	}

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		MediaType mediaType = JaxrsUtils.convertMediaType(message.getContentType());
		Annotation[] annotations = descriptor.getAnnotations();
		for (MessageBodyReader messageBodyReader : messageBodyReaders.getServices()) {
			if (messageBodyReader.isReadable(descriptor.getType(), descriptor.getResolvableType().getType(),
					annotations, mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		MediaType mediaType = JaxrsUtils.convertMediaType(message.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters.getServices()) {
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
		MediaType mediaType = JaxrsUtils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getTypeDescriptor().getAnnotations();
		MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(request.getHeaders());
		for (MessageBodyReader messageBodyReader : messageBodyReaders.getServices()) {
			if (messageBodyReader.isReadable(parameterDescriptor.getTypeDescriptor().getType(),
					parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType)) {
				return messageBodyReader.readFrom(parameterDescriptor.getTypeDescriptor().getType(),
						parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType,
						headerMap, request.getInputStream());
			}
		}
		throw new WebMessagelConverterException(parameterDescriptor, request, null);
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		MediaType mediaType = JaxrsUtils.convertMediaType(response.getContentType());
		Annotation[] annotations = typeDescriptor.getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters.getServices()) {
			if (messageBodyWriter.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
					annotations, mediaType)) {
				MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(response.getHeaders());
				// 代理response output, 因为一些实现会在调用getOutputStream后无法再设置Headers
				OutputStream proxyOutput = getProxyOutputStream(null, headerMap);
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
		MediaType mediaType = JaxrsUtils.convertMediaType(request.getContentType());
		Annotation[] annotations = parameterDescriptor.getTypeDescriptor().getAnnotations();
		for (MessageBodyWriter messageBodyWriter : messageBodyWriters.getServices()) {
			if (messageBodyWriter.isWriteable(parameterDescriptor.getTypeDescriptor().getType(),
					parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType)) {
				MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(request.getHeaders());
				OutputStream proxyOutput = getProxyOutputStream(request, headerMap);
				messageBodyWriter.writeTo(parameter, parameterDescriptor.getTypeDescriptor().getType(),
						parameterDescriptor.getTypeDescriptor().getResolvableType().getType(), annotations, mediaType,
						headerMap, proxyOutput);
				break;
			}
		}
		return request;
	}

	@Override
	public boolean isConfigured() {
		return configured;
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
	private static class OutputStreamMethodExecutionInterceptor implements ReflectionMethodExecutionInterceptor {
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
		public Object intercept(ReflectionMethodExecutor executor, Object[] args) throws Throwable {
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
