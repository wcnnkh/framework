package io.basc.framework.execution.param;

import java.util.logging.Level;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.execution.Executable;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 将多个参数抽取器组合
 * 
 * @author wcnnkh
 *
 */
public class ParameterExtractors extends ConfigurableServices<ParameterExtractor> implements ParameterExtractor {
	private static Logger logger = LoggerFactory.getLogger(ParameterExtractors.class);

	public ParameterExtractors() {
		super(ParameterExtractor.class);
	}

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
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return extractor.extractParameter(parameterDescriptor);
			}
		}
		throw new ExtractParameterException(parameterDescriptor.toString());
	}

	@Override
	public boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameters(parameterDescriptors)) {
				return true;
			}
		}

		return parameterDescriptors.index().allMatch((index) -> {
			try {
				boolean success = canExtractParameter(index.getElement());
				Level level = success ? Levels.TRACE.getValue() : Levels.DEBUG.getValue();
				if (logger.isLoggable(level)) {
					logger.log(level, "parameter {} matching: {}", index, success ? "success" : "fail");
				}
				return success;
			} catch (StackOverflowError e) {
				logger.error(e, "There are circular references parameter {}", index);
				return false;
			}
		});
	}

	@Override
	public Elements<? extends Object> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameters(parameterDescriptors)) {
				return extractor.extractParameters(parameterDescriptors);
			}
		}
		return parameterDescriptors.index().map((row) -> {
			try {
				return extractParameter(row.getElement());
			} catch (Throwable e) {
				if (e instanceof ExtractParameterException) {
					throw e;
				}
				throw new ExtractParameterException(row.toString(), e);
			}
		});
	}

	@Override
	public boolean canExtractParameters(Executable executable) throws ExtractParameterException {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameters(executable)) {
				return true;
			}
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
	public Elements<? extends Object> extractParameters(Executable executable) throws ExtractParameterException {
		for (ParameterExtractor extractor : getServices()) {
			if (extractor.canExtractParameters(executable)) {
				return extractor.extractParameters(executable);
			}
		}

		return executable.getParameterDescriptors().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			try {
				return extractParameter(parameterDescriptor);
			} catch (Throwable e) {
				if (e instanceof ExtractParameterException) {
					throw e;
				}
				throw new ExtractParameterException(
						executable + " parameter index " + row.getIndex() + " descriptor " + parameterDescriptor, e);
			}
		});
	}
}
