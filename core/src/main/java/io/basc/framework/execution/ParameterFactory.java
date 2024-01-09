package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

public interface ParameterFactory {
	/**
	 * 是否能获取参数
	 * 
	 * @param source
	 * @param parameterDescriptor
	 * @return
	 */
	boolean canExtractParameter(ParameterDescriptor parameterDescriptor);

	/**
	 * 获取参数
	 * 
	 * @param source
	 * @param parameterDescriptor
	 * @return
	 */
	Object extractParameter(ParameterDescriptor parameterDescriptor);

	default boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch((e) -> canExtractParameter(e));
	}

	default Elements<Parameter> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.index().map((e) -> new Parameter((int) e.getIndex(), e.getElement().getName(),
				extractParameter(e.getElement()), e.getElement().getTypeDescriptor()));
	}

	default boolean canExtractExecutionParameters(Executable executable) {
		return canExtractParameters(executable.getParameterDescriptors());
	}

	default Parameters extractExecutionParameters(Executable executable) {
		Parameters parameters = new Parameters();
		parameters.setElements(extractParameters(executable.getParameterDescriptors()));
		return parameters;
	}
}
