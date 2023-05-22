package io.basc.framework.factory;

import java.util.logging.Level;

import io.basc.framework.execution.parameter.ParameterException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.Elements;

public interface ParameterFactory extends ParametersFactory {
	/**
	 * 用于默认实现的日志
	 */
	static Logger $log = LoggerFactory.getLogger(ParameterFactory.class);

	boolean isAccept(ParameterDescriptor parameterDescriptor);

	@Nullable
	Object getParameter(ParameterDescriptor parameterDescriptor);

	default boolean isAccept(ParameterDescriptors parameterDescriptors) {
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors.getElements()) {
			try {
				boolean auto = isAccept(parameterDescriptor);
				Level level = auto ? Levels.TRACE.getValue() : Levels.DEBUG.getValue();
				if ($log.isLoggable(level)) {
					$log.log(level, "{} parameter index {} matching: {}", parameterDescriptors.getSource(), index,
							auto ? "success" : "fail");
				}
				if (!auto) {
					return false;
				}
			} catch (StackOverflowError e) {
				$log.error(e, "There are circular references clazz [{}] parameterName [{}] in [{}]",
						parameterDescriptors.getSourceClass(), parameterDescriptor.getName(),
						parameterDescriptors.getSource());
				return false;
			} finally {
				index++;
			}
		}
		return true;
	}

	default Elements<? extends Parameter> getParameters(ParameterDescriptors parameterDescriptors) {
		return parameterDescriptors.getElements().index().map((row) -> {
			ParameterDescriptor parameterDescriptor = row.getElement();
			try {
				Object value = getParameter(parameterDescriptor);
				return new Parameter(parameterDescriptor.getName(), value, parameterDescriptor.getTypeDescriptor());
			} catch (Exception e) {
				throw new ParameterException(parameterDescriptors.getSource() + " parameter index " + row.getIndex()
						+ " descriptor " + parameterDescriptor, e);
			}
		});
	}
}
