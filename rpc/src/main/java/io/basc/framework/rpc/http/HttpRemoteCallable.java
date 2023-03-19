package io.basc.framework.rpc.http;

import java.net.URI;
import java.util.concurrent.Callable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.pattern.HttpPattern;

final class HttpRemoteCallable implements Callable<Object> {
	private static Logger logger = LoggerFactory.getLogger(HttpRemoteCallable.class);
	private final WebMessageConverter webMessageConverter;
	private final HttpClient httpClient;
	private final HttpPattern httpPattern;
	private final ParameterDescriptor[] parameterDescriptors;
	private final Object[] args;
	private final TypeDescriptor returnType;
	private final URI host;

	public HttpRemoteCallable(WebMessageConverter webMessageConverter, HttpClient httpClient, URI host,
			HttpPattern httpPattern, ParameterDescriptor[] parameterDescriptors, Object[] args,
			TypeDescriptor returnType) {
		this.webMessageConverter = webMessageConverter;
		this.host = host;
		this.httpClient = httpClient;
		this.httpPattern = httpPattern;
		this.parameterDescriptors = parameterDescriptors;
		this.args = args;
		this.returnType = returnType;
	}

	@Override
	public Object call() throws Exception {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(host).path(httpPattern.getPath());
		if (parameterDescriptors != null) {
			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				builder = webMessageConverter.write(builder, parameterDescriptor, args[i++]);
			}
		}

		URI uri = builder.build().toUri();
		return httpClient.execute(uri, httpPattern.getMethod(), (request) -> {
			String messageId = WebUtils.getMessageId(request);
			if (httpPattern.hasConsumes()) {
				request.getHeaders().put(HttpHeaders.CONTENT_TYPE, httpPattern.getConsumes().getRawMimeTypes());
			}

			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				request = webMessageConverter.write(request, parameterDescriptor, args[i++]);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("message[{}] request {} {}", messageId, request.getRawMethod(), uri);
			}
			return request;
		}, (request, response) -> {
			Object value = webMessageConverter.read(response, returnType);
			if (logger.isDebugEnabled()) {
				logger.debug("message[{}] response: {}", WebUtils.getMessageId(request), value);
			}
			return value;
		}).getBody();
	}
}
