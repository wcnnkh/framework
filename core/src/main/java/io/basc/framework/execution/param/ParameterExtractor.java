package io.basc.framework.execution.param;

import io.basc.framework.execution.Executable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

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
	 * @throws ExtractParameterException
	 */
	Object extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException;

	default boolean canExtractParameters(ParameterDescriptor[] parameterDescriptors) {
		return Elements.forArray(parameterDescriptors).allMatch(this::canExtractParameter);
	}

	default Object[] extractParameters(ParameterDescriptor[] parameterDescriptors) throws ExtractParameterException {
		return Elements.forArray(parameterDescriptors).map(this::extractParameter).toArray();
	}

	default boolean canExtractParameters(Executable executable) {
		return canExtractParameters(executable.getParameterDescriptors());
	}

	default Object[] extractParameters(Executable executable) throws ExtractParameterException {
		return extractParameters(executable.getParameterDescriptors());
	}
}
