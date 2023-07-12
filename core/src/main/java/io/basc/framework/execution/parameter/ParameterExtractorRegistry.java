package io.basc.framework.execution.parameter;

import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Services;

/**
 * 将多个参数抽取器组合
 * 
 * @author wcnnkh
 *
 */
public class ParameterExtractorRegistry extends Services<ParameterExtractor> implements ParameterExtractor {

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return extractor.extractParameter(parameterDescriptor);
			}
		}
		throw new UnsupportedException(parameterDescriptor.getName());
	}

}
