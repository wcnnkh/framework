package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceRegistry;

public class ExecutableParametersExtractorRegistry extends ServiceRegistry<ExecutableParametersExtractor>
		implements ExecutableParametersExtractor {

	@Override
	public boolean canExtractExecutionParameters(Executor executable) {
		for (ExecutableParametersExtractor extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executable)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executable) throws ParameterException {
		for (ExecutableParametersExtractor extractor : getServices()) {
			if (extractor.canExtractExecutionParameters(executable)) {
				return extractor.extractExecutionParameters(executable);
			}
		}
		throw new ParameterException("Unable to obtain parameters: " + executable);
	}

}
