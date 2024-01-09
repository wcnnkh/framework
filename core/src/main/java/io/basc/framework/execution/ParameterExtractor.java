package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

/**
 * 参数提取器
 * 
 * @author wcnnkh
 *
 */
public interface ParameterExtractor<S> {

	/**
	 * 是否能获取参数
	 * 
	 * @param source
	 * @param parameterDescriptor
	 * @return
	 */
	boolean canExtractParameter(S source, ParameterDescriptor parameterDescriptor);

	/**
	 * 获取参数
	 * 
	 * @param source
	 * @param parameterDescriptor
	 * @return
	 * @throws ExtractParameterException
	 */
	Object extractParameter(S source, ParameterDescriptor parameterDescriptor);

	default boolean canExtractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch((e) -> canExtractParameter(source, e));
	}

	default Elements<Parameter> extractParameters(S source,
			Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.index().map((e) -> new Parameter((int) e.getIndex(), e.getElement().getName(),
				extractParameter(source, e.getElement()), e.getElement().getTypeDescriptor()));
	}

	default boolean canExtractExecutionParameters(S source, Executable executable) {
		return canExtractParameters(source, executable.getParameterDescriptors());
	}

	default Parameters extractExecutionParameters(S source, Executable executable) {
		Parameters parameters = new Parameters();
		parameters.setElements(extractParameters(source, executable.getParameterDescriptors()));
		return parameters;
	}
}
