package io.basc.framework.execution.param;

import java.util.logging.Level;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.execution.Executor;
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
public class ParameterFactories extends ConfigurableServices<ParameterFactory> implements ParameterFactory {
	private static Logger logger = LoggerFactory.getLogger(ParameterFactories.class);

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
	public Parameter extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameter(parameterDescriptor)) {
				return extractor.extractParameter(parameterDescriptor);
			}
		}
		throw new ExtractParameterException(parameterDescriptor.toString());
	}

	@Override
	public boolean canExtractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterFactory extractor : getServices()) {
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
	public Elements<Parameter> extractParameters(Elements<? extends ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameters(parameterDescriptors)) {
				return extractor.extractParameters(parameterDescriptors);
			}
		}
		return parameterDescriptors.index().map((row) -> {
			Parameter parameter;
			try {
				parameter = extractParameter(row.getElement());
			} catch (Throwable e) {
				if (e instanceof ExtractParameterException) {
					throw e;
				}
				throw new ExtractParameterException(row.toString(), e);
			}
			parameter.setIndex((int) row.getIndex());
			return parameter;
		});
	}

	@Override
	public boolean canExtractParameters(Executor executor) throws ExtractParameterException {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameters(executor)) {
				return true;
			}
		}

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
	public Parameters extractParameters(Executor executor) throws ExtractParameterException {
		for (ParameterFactory extractor : getServices()) {
			if (extractor.canExtractParameters(executor)) {
				return extractor.extractParameters(executor);
			}
		}

		Parameters parameters = new Parameters();
		parameters.setElements(executor.getParameterDescriptors().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			Parameter parameter;
			try {
				parameter = extractParameter(parameterDescriptor);
			} catch (Throwable e) {
				if (e instanceof ExtractParameterException) {
					throw e;
				}
				throw new ExtractParameterException(
						executor + " parameter index " + row.getIndex() + " descriptor " + parameterDescriptor, e);
			}
			parameter.setIndex((int) row.getIndex());
			return parameter;
		}));
		return parameters;
	}
}
