package io.basc.framework.mvc.message;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestWrapper;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;
import java.net.URI;

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

	/**
	 * 控制着以下行为{@link #read(ServerHttpRequest, ParameterDescriptor)} and
	 * {@link #write(ClientHttpRequest, ParameterDescriptor, Object)}
	 * 
	 * @param request
	 * @param parameterDescriptor
	 * @return
	 */
	default boolean isAccept(HttpRequest request, ParameterDescriptor parameterDescriptor) {
		return isAccept(parameterDescriptor);
	}

	/**
	 * 读取内容
	 * 
	 * @param request
	 * @param parameterDescriptor
	 * @return
	 * @throws IOException
	 * @throws WebMessagelConverterException
	 * @see #isAccept(HttpRequest, ParameterDescriptor)
	 */
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
	 * @see #isAccept(HttpRequest, ParameterDescriptor)
	 */
	default ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor,
			Object parameter) throws IOException, WebMessagelConverterException {
		return request;
	}

	/**
	 * 根据参数构造uri
	 * 
	 * @param uri
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 * @see #isAccept(ParameterDescriptor)
	 */
	default UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		return builder;
	}

	/**
	 * 控制着以下行为{@link #read(ClientHttpResponse, TypeDescriptor)} and
	 * {@link #write(ServerHttpRequest, ServerHttpResponse, TypeDescriptor, Object)}
	 * 
	 * @param message        可能是{@link ClientHttpResponse} or
	 *                       {@link ServerHttpRequest}
	 * @param typeDescriptor
	 * @return 是否可以处理
	 */
	boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor);

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException;

	/**
	 * 控制着以下行为{@link #write(ServerHttpRequest, ServerHttpResponse, TypeDescriptor, Object)}}
	 * 
	 * @param request
	 * @param typeDescriptor
	 * @param body
	 * @return
	 */
	default boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor, Object body) {
		return isAccept(message, typeDescriptor);
	}

	/**
	 * 写入
	 * 
	 * @param request
	 * @param response
	 * @param typeDescriptor
	 * @param body
	 * @throws IOException
	 * @throws WebMessagelConverterException
	 * @see #isAccept(HttpMessage, TypeDescriptor, Object)
	 */
	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body)
			throws IOException, WebMessagelConverterException;
}
