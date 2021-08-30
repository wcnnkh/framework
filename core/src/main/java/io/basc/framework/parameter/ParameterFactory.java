package io.basc.framework.parameter;

import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.util.logging.Level;

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
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
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
						parameterDescriptors.getDeclaringClass(), parameterDescriptor.getName(),
						parameterDescriptors.getSource());
				return false;
			} finally {
				index++;
			}
		}
		return true;
	}

	default Object[] getParameters(ParameterDescriptors parameterDescriptors) {
		Object[] args = new Object[parameterDescriptors.size()];
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			try {
				args[index] = getParameter(parameterDescriptor);
			} catch (Exception e) {
				throw new ParameterException(parameterDescriptors.getSource() + " parameter index " + index
						+ " descriptor " + parameterDescriptor, e);
			}
			index++;
		}
		return args;
	}
}
