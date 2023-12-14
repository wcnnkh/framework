package io.basc.framework.execution.param;

import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
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
	 * @throws ExtractParameterException
	 */
	Parameter extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException;

	default boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch((e) -> canExtractParameter(e));
	}

	default Elements<Parameter> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		return parameterDescriptors.index().map((e) -> {
			Parameter parameter = extractParameter(e.getElement());
			parameter.setIndex((int) e.getIndex());
			return parameter;
		});
	}

	default boolean canExtractParameters(Executor executor) {
		return canExtractParameters(executor.getParameterDescriptors());
	}

	default Parameters extractParameters(Executor executor) throws ExtractParameterException {
		Parameters parameters = new Parameters();
		parameters.setElements(extractParameters(executor.getParameterDescriptors()));
		return parameters;
	}
}
