package io.basc.framework.execution.param;

import java.util.logging.Level;

import io.basc.framework.execution.Executor;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.Elements;

/**
 * 将多个参数抽取器组合
 * 
 * @author wcnnkh
 *
 */
public class ParameterExtractors<S> extends ServiceRegistry<ParameterExtractor<? super S>>
		implements ParameterExtractor<S> {
	private static Logger logger = LoggerFactory.getLogger(ParameterExtractors.class);

	@Override
	public boolean canExtractParameter(S source, ParameterDescriptor parameterDescriptor) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameter(source, parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors) {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, parameterDescriptors)) {
				return true;
			}
		}

		return parameterDescriptors.index().allMatch((index) -> {
			try {
				boolean success = canExtractParameter(source, index.getElement());
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
	public boolean canExtractParameters(S source, Executor executor) throws ExtractParameterException {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, executor)) {
				return true;
			}
		}

		return executor.getParameterDescriptors().index().allMatch((index) -> {
			try {
				boolean success = canExtractParameter(source, index.getElement());
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
	public Parameter extractParameter(S source, ParameterDescriptor parameterDescriptor)
			throws ExtractParameterException {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameter(source, parameterDescriptor)) {
				return extractor.extractParameter(source, parameterDescriptor);
			}
		}
		throw new ExtractParameterException(parameterDescriptor.toString());
	}

	@Override
	public Elements<Parameter> extractParameters(S source, Elements<? extends ParameterDescriptor> parameterDescriptors)
			throws ExtractParameterException {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, parameterDescriptors)) {
				return extractor.extractParameters(source, parameterDescriptors);
			}
		}
		return parameterDescriptors.index().map((row) -> {
			Parameter parameter;
			try {
				parameter = extractParameter(source, row.getElement());
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
	public Parameters extractParameters(S source, Executor executor) throws ExtractParameterException {
		for (ParameterExtractor<? super S> extractor : getServices()) {
			if (extractor.canExtractParameters(source, executor)) {
				return extractor.extractParameters(source, executor);
			}
		}

		Parameters parameters = new Parameters();
		parameters.setElements(executor.getParameterDescriptors().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			Parameter parameter;
			try {
				parameter = extractParameter(source, parameterDescriptor);
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
