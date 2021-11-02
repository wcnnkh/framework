package io.basc.framework.web.message;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.pattern.HttpPattern;

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

	Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor);

	/**
	 * 写入并返回request(可能是一个新的)
	 * 
	 * @param output
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 */
	ClientHttpRequest write(ClientHttpRequest output, ParameterDescriptor parameterDescriptor, Object parameter);

	URI write(URI uri, ParameterDescriptor parameterDescriptor, Object parameter);

	/**
	 * 控制着以下行为{@link #read(ClientHttpResponse, TypeDescriptor)} and
	 * {@link #write(ServerHttpRequest, ServerHttpResponse, TypeDescriptor, Object)}
	 * 
	 * @param typeDescriptor
	 * @return 是否可以处理
	 */
	boolean isAccept(TypeDescriptor typeDescriptor);

	Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor);

	void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor, Object body);

	/**
	 * 如果此方法返回false就没必要调用{@link #canResolvePatterns(Class, Method)}
	 * 
	 * @param clazz
	 * @return 是否可以处理
	 */
	boolean canResolvePatterns(Class<?> clazz);

	/**
	 * 如果返回true就可以调用{@link #resolvePatterns(Class, Method)}
	 * 
	 * @param clazz
	 * @param method
	 * @return 是否可以处理
	 */
	boolean canResolvePatterns(Class<?> clazz, Method method);

	Collection<HttpPattern> resolvePatterns(Class<?> clazz, Method method);
}
