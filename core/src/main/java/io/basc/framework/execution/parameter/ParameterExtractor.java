package io.basc.framework.execution.parameter;

import io.basc.framework.mapper.ParameterDescriptor;

/**
 * 参数提取器
 * 
 * @author wcnnkh
 *
 */
public interface ParameterExtractor {
	/**
	 * 是否能获取参数
	 * 
	 * @param parameterDescriptor
	 * @return
	 */
	boolean canExtractParameter(ParameterDescriptor parameterDescriptor);

	/**
	 * 获取参数
	 * 
	 * @param parameterDescriptor
	 * @return
	 * @throws ParameterException
	 */
	Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException;
}
