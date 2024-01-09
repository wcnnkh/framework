package io.basc.framework.execution;

import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

/**
 * 将多个参数抽取器组合
 * 
 * @author wcnnkh
 *
 */
public class ParameterExtractors<S> extends ServiceRegistry<ParameterExtractor<? super S>>
		implements ParameterExtractor<S> {

	@Override
	public boolean canExtractParameter(S source, ParameterDescriptor parameterDescriptor) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameter(source, parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, parameterDescriptors)) {
				return true;
			}
		}
		return ParameterExtractor.super.canExtractParameters(source, parameterDescriptors);
	}

	@Override
	public Object extractParameter(S source, ParameterDescriptor parameterDescriptor) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameter(source, parameterDescriptor)) {
				return extractor.extractParameter(source, parameterDescriptor);
			}
		}
		throw new UnsupportedException(parameterDescriptor.toString());
	}

	@Override
	public Elements<Parameter> extractParameters(S source,
			Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, parameterDescriptors)) {
				return extractor.extractParameters(source, parameterDescriptors);
			}
		}
		return ParameterExtractor.super.extractParameters(source, parameterDescriptors);
	}

	@Override
	public boolean canExtractExecutionParameters(S source, Executable executable) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(source, executable)) {
				return extractor.canExtractExecutionParameters(source, executable);
			}
		}
		return ParameterExtractor.super.canExtractExecutionParameters(source, executable);
	}

	@Override
	public Parameters extractExecutionParameters(S source, Executable executable) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(source, executable)) {
				return extractor.extractExecutionParameters(source, executable);
			}
		}
		return ParameterExtractor.super.extractExecutionParameters(source, executable);
	}
}
