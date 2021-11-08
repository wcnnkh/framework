package io.basc.framework.web.message;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;

/**
 * @see DefaultWebMessageConverters
 * @author shuchaowen
 *
 */
public interface WebMessageConverter {
	boolean canRead(HttpMessage message, TypeDescriptor descriptor);

	Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException;

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException;

	boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value);

	/**
	 * 写入
	 * 
	 * @param request
	 * @param response
	 * @param typeDescriptor
	 * @param body
	 * @throws IOException
	 * @throws WebMessagelConverterException
	 */
	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body)
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

	default boolean canBuildUri(TypeDescriptor typeDescriptor, Object parameter) {
		return false;
	}

	/**
	 * 根据参数构造uri
	 * 
	 * @param builder
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 */
	default UriComponentsBuilder buildUri(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}
}
