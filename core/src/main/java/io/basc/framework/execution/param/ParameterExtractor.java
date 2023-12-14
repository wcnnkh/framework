package io.basc.framework.execution.param;

import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

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
	Parameter extractParameter(S source, ParameterDescriptor parameterDescriptor) throws ExtractParameterException;

	default boolean canExtractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch((e) -> canExtractParameter(source, e));
	}

	default Elements<Parameter> extractParameters(S source,
			Elements<? extends ParameterDescriptor> parameterDescriptors) throws ExtractParameterException {
		return parameterDescriptors.index().map((e) -> {
			Parameter parameter = extractParameter(source, e.getElement());
			parameter.setIndex((int) e.getIndex());
			return parameter;
		});
	}

	default boolean canExtractParameters(S source, Executor executor) {
		return canExtractParameters(source, executor.getParameterDescriptors());
	}

	default Parameters extractParameters(S source, Executor executor) throws ExtractParameterException {
		Parameters parameters = new Parameters();
		parameters.setElements(extractParameters(source, executor.getParameterDescriptors()));
		return parameters;
	}
}
