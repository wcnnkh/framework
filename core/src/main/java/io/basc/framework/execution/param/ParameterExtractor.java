package io.basc.framework.execution.param;

import io.basc.framework.execution.Executor;
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

	default boolean canExtractParameters(Elements<ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch(this::canExtractParameter);
	}

	default Elements<Object> extractParameters(Elements<ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		return parameterDescriptors.map(this::extractParameter);
	}

	default boolean canExtractParameters(Executor executor) {
		return canExtractParameters(executor.getParameterDescriptors());
	}

	default Elements<Object> extractParameters(Executor executor) throws ExtractParameterException {
		return extractParameters(executor.getParameterDescriptors());
	}
}
