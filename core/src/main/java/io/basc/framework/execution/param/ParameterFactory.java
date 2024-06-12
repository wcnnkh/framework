package io.basc.framework.execution.param;

import io.basc.framework.execution.Executable;
import io.basc.framework.util.element.Elements;

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
		return parameterDescriptors.index()
				.map((indexed) -> new Arg(indexed.getElement(), extractParameter(indexed.getElement())));
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
