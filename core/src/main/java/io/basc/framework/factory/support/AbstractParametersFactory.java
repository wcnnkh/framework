package io.basc.framework.factory.support;

import java.util.logging.Level;

import io.basc.framework.factory.ParametersFactory;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;

public abstract class AbstractParametersFactory implements ParametersFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractParametersFactory.class);

	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			try {
				boolean auto = isAccept(parameterDescriptors, parameterDescriptor, index);
				Level level = auto ? Levels.TRACE.getValue() : Levels.DEBUG.getValue();
				if (logger.isLoggable(level)) {
					logger.log(level, "{} parameter index {} matching: {}", parameterDescriptors.getSource(), index,
							auto ? "success" : "fail");
				}
				if (!auto) {
					return false;
				}
			} catch (StackOverflowError e) {
				logger.error(e, "There are circular references clazz [{}] parameterName [{}] in [{}]",
						parameterDescriptors.getDeclaringClass(), parameterDescriptor.getName(),
						parameterDescriptors.getSource());
				return false;
			} finally {
				index++;
			}
		}
		return true;
	}

	protected abstract boolean isAccept(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor, int index);

	public Object[] getParameters(ParameterDescriptors parameterDescriptors) {
		Object[] args = new Object[parameterDescriptors.size()];
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			try {
				args[index] = getParameter(parameterDescriptors, parameterDescriptor, index);
			} catch (Exception e) {
				throw new ParameterException(parameterDescriptors.getSource() + " parameter index " + index
						+ " descriptor " + parameterDescriptor, e);
			}
			index++;
		}
		return args;
	}

	protected abstract Object getParameter(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor, int index) throws Exception;
}
