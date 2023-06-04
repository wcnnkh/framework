package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutionParametersExtractors implements ExecutionParametersExtractor {
	private final Elements<? extends ExecutionParametersExtractor> executionParametersExtractors;

	@Override
	public boolean canExtractExecutionParameters(Executor executor) {
		for (ExecutionParametersExtractor executionParametersExtractor : executionParametersExtractors) {
			if (executionParametersExtractor.canExtractExecutionParameters(executor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executor) throws ParameterException {
		for (ExecutionParametersExtractor executionParametersExtractor : executionParametersExtractors) {
			if (executionParametersExtractor.canExtractExecutionParameters(executor)) {
				return executionParametersExtractor.extractExecutionParameters(executor);
			}
		}
		throw new UnsupportedException(executor.getName());
	}

}
