package io.basc.framework.execution.parameter;

import java.util.logging.Level;

import io.basc.framework.execution.Executor;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

public class ParametersExtractorRegistry extends ExecutableParametersExtractorRegistry implements ParameterExtractor {
	private static Logger logger = LoggerFactory.getLogger(ParametersExtractorRegistry.class);
	private final ParameterExtractorRegistry parameterExtractorRegistry = new ParameterExtractorRegistry();

	@Override
	public boolean canExtractExecutionParameters(Executor executable) {
		if (super.canExtractExecutionParameters(executable)) {
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

	public ParameterExtractorRegistry getParameterExtractorRegistry() {
		return parameterExtractorRegistry;
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executable) throws ParameterException {
		if (super.canExtractExecutionParameters(executable)) {
			return super.extractExecutionParameters(executable);
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

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		return parameterExtractorRegistry.canExtractParameter(parameterDescriptor);
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException {
		return parameterExtractorRegistry.extractParameter(parameterDescriptor);
	}

}
