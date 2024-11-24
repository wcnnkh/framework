package io.basc.framework.core.execution.param;

import io.basc.framework.core.execution.Executable;
import io.basc.framework.util.Elements;

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
		return parameterDescriptors.index().map((indexed) -> {
			SimpleParameter parameter = new SimpleParameter(indexed.getElement());
			parameter.setValue(extractParameter(indexed.getElement()));
			return parameter;
		});
	}

	default boolean canExtractExecutionParameters(Executable executable) {
		return canExtractParameters(executable.getParameterDescriptors());
	}

	default Parameters extractExecutionParameters(Executable executable) {
		Args parameters = new Args();
		parameters.setElements(extractParameters(executable.getParameterDescriptors()));
		return parameters;
	}
}
