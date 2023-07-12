package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Services;

public class ExecutionParametersExtractorRegistry extends Services<ExecutionParametersExtractor>
		implements ExecutionParametersExtractor {

	@Override
	public boolean canExtractExecutionParameters(Executor executor) {
		for (ExecutionParametersExtractor extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executor) throws ParameterException {
		for (ExecutionParametersExtractor extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executor)) {
				return extractor.extractExecutionParameters(executor);
			}
		}
		throw new ParameterException("Unable to obtain parameters: " + executor);
	}

}
