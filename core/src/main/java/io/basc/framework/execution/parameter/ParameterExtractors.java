package io.basc.framework.execution.parameter;

import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParameterExtractors implements ParameterExtractor {
	private final Elements<? extends ParameterExtractor> parameterExtractors;

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		for (ParameterExtractor parameterExtractor : parameterExtractors) {
			if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException {
		for (ParameterExtractor parameterExtractor : parameterExtractors) {
			if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
				return parameterExtractor.extractParameter(parameterDescriptor);
			}
		}
		throw new UnsupportedException(parameterDescriptor.getName());
	}

}
