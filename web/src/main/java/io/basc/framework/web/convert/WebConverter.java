package io.basc.framework.web.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.net.ClientRequest;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.Message;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.web.message.WebMessagelConverterException;

public interface WebConverter {
	/**
	 * 是否可以读取此参数
	 * 
	 * @param message
	 * @param typeDescriptor
	 * @return
	 */
	boolean canRead(Message message, TypeDescriptor typeDescriptor);

	/**
	 * 读取参数
	 * 
	 * @param request
	 * @param parameterDescriptor
	 * @return
	 * @throws IOException
	 * @throws WebMessagelConverterException
	 */
	Object read(InputMessage request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebConverterException;

	/**
	 * 是否可以写入
	 * 
	 * @param typeDescriptor
	 * @param value
	 * @return
	 */
	boolean canWrite(TypeDescriptor typeDescriptor, Object value);

	/**
	 * 写入一个消息体
	 * 
	 * @param input
	 * @param output
	 * @param typeDescriptor
	 * @param body
	 * @throws IOException
	 * @throws WebConverterException
	 */
	void write(Message input, OutputMessage output, TypeDescriptor typeDescriptor, Object body)
			throws IOException, WebConverterException;

	/**
	 * 向客户端请求追加写入
	 * 
	 * @param request
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 * @throws IOException
	 * @throws WebConverterException
	 */
	ClientRequest appendWrite(ClientRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebConverterException;

	/**
	 * 向url添加参数
	 * 
	 * @param builder
	 * @param parameterDescriptor
	 * @param parameter
	 * @return
	 * @throws WebConverterException
	 */
	default UriComponentsBuilder appendUri(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebConverterException {
		return builder;
	}
}
