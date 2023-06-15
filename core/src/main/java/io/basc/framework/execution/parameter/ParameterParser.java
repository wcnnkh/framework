package io.basc.framework.execution.parameter;

import java.util.logging.Level;

import io.basc.framework.execution.Executor;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.Getter;

@Getter
public class ParameterParser extends ParameterExtractorRegistry implements ExecutionParametersExtractor {
	private static Logger logger = LoggerFactory.getLogger(ParameterParser.class);
	private final ExecutionParametersExtractorRegistry executionParametersExtractorRegistry = new ExecutionParametersExtractorRegistry();

	@Override
	public boolean canExtractExecutionParameters(Executor executable) {
		if (executionParametersExtractorRegistry.canExtractExecutionParameters(executable)) {
			return true;
		}

		return executable.getParameterDescriptors().index().allMatch((index) -> {
			try {
				boolean success = canExtractParameter(index.getElement());
				Level level = success ? Levels.TRACE.getValue() : Levels.DEBUG.getValue();
				if (logger.isLoggable(level)) {
					logger.log(level, "{} parameter index {} matching: {}", executable, index.getIndex(),
							success ? "success" : "fail");
				}
				return success;
			} catch (StackOverflowError e) {
				logger.error(e, "There are circular references parameterName [{}] in [{}]",
						index.getElement().getName(), executable);
				return false;
			}
		});
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executable) throws ParameterException {
		if (executionParametersExtractorRegistry.canExtractExecutionParameters(executable)) {
			return executionParametersExtractorRegistry.extractExecutionParameters(executable);
		}

		return executable.getParameterDescriptors().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			try {
				return extractParameter(parameterDescriptor);
			} catch (Exception e) {
				throw new ParameterException(
						executable + " parameter index " + row.getIndex() + " descriptor " + parameterDescriptor, e);
			}
		});
	}
}
