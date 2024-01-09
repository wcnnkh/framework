package io.basc.framework.execution;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

/**
 * 将多个参数抽取器组合
 * 
 * @author wcnnkh
 *
 */
public class ParameterFactories extends ConfigurableServices<ParameterFactory> implements ParameterFactory {

	public ParameterFactories() {
		super(ParameterFactory.class);
	}

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return extractor.extractParameter(parameterDescriptor);
			}
		}
		throw new UnsupportedException(parameterDescriptor.toString());
	}

	@Override
	public boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameters(parameterDescriptors)) {
				return true;
			}
		}
		return ParameterFactory.super.canExtractParameters(parameterDescriptors);
	}

	@Override
	public Elements<Parameter> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameters(parameterDescriptors)) {
				return extractor.extractParameters(parameterDescriptors);
			}
		}
		return ParameterFactory.super.extractParameters(parameterDescriptors);
	}

	@Override
	public boolean canExtractExecutionParameters(Executable executable) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executable)) {
				return extractor.canExtractExecutionParameters(executable);
			}
		}
		return ParameterFactory.super.canExtractExecutionParameters(executable);
	}

	@Override
	public Parameters extractExecutionParameters(Executable executable) {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executable)) {
				return extractor.extractExecutionParameters(executable);
			}
		}
		return ParameterFactory.super.extractExecutionParameters(executable);
	}
}
