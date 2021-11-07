package io.basc.framework.rpc.http;

import java.net.URI;
import java.util.concurrent.Callable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.pattern.HttpPattern;

public class HttpRemoteCallable implements Callable<Object> {
	private final WebMessageConverter webMessageConverter;
	private final ClientHttpRequestFactory clientHttpRequestFactory;
	private final HttpPattern httpPattern;
	private final ParameterDescriptor[] parameterDescriptors;
	private final Object[] args;
	private final TypeDescriptor returnType;

	public HttpRemoteCallable(WebMessageConverter webMessageConverter,
			ClientHttpRequestFactory clientHttpRequestFactory, HttpPattern httpPattern,
			ParameterDescriptor[] parameterDescriptors, Object[] args, TypeDescriptor returnType) {
		this.webMessageConverter = webMessageConverter;
		this.clientHttpRequestFactory = clientHttpRequestFactory;
		this.httpPattern = httpPattern;
		this.parameterDescriptors = parameterDescriptors;
		this.args = args;
		this.returnType = returnType;
	}

	@Override
	public Object call() throws Exception {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(httpPattern.getPath());
		if (parameterDescriptors != null) {
			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				builder = webMessageConverter.write(builder, parameterDescriptor, args[i++]);
			}
		}

		URI uri = builder.build().toUri();
		ClientHttpRequest request = clientHttpRequestFactory.createRequest(uri, httpPattern.getMethod());
		if (parameterDescriptors != null) {
			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				request = webMessageConverter.write(request, parameterDescriptor, args[i]);
			}
		}

		ClientHttpResponse response = request.execute();
		try {
			return webMessageConverter.read(response, returnType);
		} finally {
			response.close();
		}
	}

}
