package io.basc.framework.execution.param;

import io.basc.framework.execution.Executable;
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
	Object extractParameter(S source, ParameterDescriptor parameterDescriptor);

	default boolean canExtractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.allMatch((e) -> canExtractParameter(source, e));
	}

	default Elements<Parameter> extractParameters(S source,
			Elements<? extends ParameterDescriptor> parameterDescriptors) {
		return parameterDescriptors.index().map((e) -> {
			SimpleParameter parameter = new SimpleParameter(e.getElement());
			parameter.setValue(extractParameter(source, e.getElement()));
			return parameter;
		});
	}

	default boolean canExtractExecutionParameters(S source, Executable executable) {
		return canExtractParameters(source, executable.getParameterDescriptors());
	}

	default Parameters extractExecutionParameters(S source, Executable executable) {
		Args parameters = new Args();
		parameters.setElements(extractParameters(source, executable.getParameterDescriptors()));
		return parameters;
	}
}
