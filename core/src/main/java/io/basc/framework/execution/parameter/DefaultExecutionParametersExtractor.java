package io.basc.framework.execution.parameter;

import java.util.logging.Level;

import io.basc.framework.execution.Executor;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

public class DefaultExecutionParametersExtractor extends ParameterExtractorRegistry
		implements ExecutionParametersExtractor {
	/**
	 * 用于默认实现的日志
	 */
	private static Logger logger = LoggerFactory.getLogger(ParameterExtractor.class);

	@Override
	public boolean canExtractExecutionParameters(Executor executor) {
		return executor.getParameterDescriptors().index().allMatch((index) -> {
			try {
				boolean success = canExtractParameter(index.getElement());
				Level level = success ? Levels.TRACE.getValue() : Levels.DEBUG.getValue();
				if (logger.isLoggable(level)) {
					logger.log(level, "{} parameter index {} matching: {}", executor, index.getIndex(),
							success ? "success" : "fail");
				}
				return success;
			} catch (StackOverflowError e) {
				logger.error(e, "There are circular references parameterName [{}] in [{}]",
						index.getElement().getName(), executor);
				return false;
			}
		});
	}

	@Override
	public Elements<? extends Object> extractExecutionParameters(Executor executor) throws ParameterException {
		return executor.getParameterDescriptors().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			try {
				return extractParameter(parameterDescriptor);
			} catch (Exception e) {
				throw new ParameterException(
						executor + " parameter index " + row.getIndex() + " descriptor " + parameterDescriptor, e);
			}
		});
	}

}
