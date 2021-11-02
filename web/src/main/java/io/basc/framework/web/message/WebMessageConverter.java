package io.basc.framework.web.message;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public interface WebMessageConverter {
	/**
	 * 控制着以下行为{@link #read(ServerHttpRequest, ParameterDescriptor)} and
	 * {@link #write(ClientHttpRequest, ParameterDescriptor, Object)} and
	 * {@link #write(URI, ParameterDescriptor, Object)}
	 * 
	 * @param parameterDescriptor
	 * @return 是否可以处理
	 */
	boolean isAccept(ParameterDescriptor parameterDescriptor);

	Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException;

	/**
	 * 写入并返回request(可能是一个新的)
	 * 
	 * @see ClientHttpRequestWrapper
	 * @param request
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 */
	ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException;

	/**
	 * @param uri
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 */
	UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException;

	/**
	 * 控制着以下行为{@link #read(ClientHttpResponse, TypeDescriptor)} and
	 * {@link #write(ServerHttpRequest, ServerHttpResponse, TypeDescriptor, Object)}
	 * 
	 * @param typeDescriptor
	 * @return 是否可以处理
	 */
	boolean isAccept(TypeDescriptor typeDescriptor);

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException;

	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body)
			throws IOException, WebMessagelConverterException;
}
