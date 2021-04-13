package scw.core.parameter;

import scw.lang.ParameterException;
import scw.logger.Level;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractParameterFactory implements ParameterFactory {
	private static Logger logger = LoggerUtils
			.getLogger(AbstractParameterFactory.class);

	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			try {
				boolean auto = isAccept(parameterDescriptors, parameterDescriptor, index);
				logger.log(auto ? Level.TRACE.getValue() : Level.DEBUG.getValue(),
						"{} parameter index {} matching: {}",
						parameterDescriptors.getSource(), index,
						auto ? "success" : "fail");
				if (!auto) {
					return false;
				}
			} catch (StackOverflowError e) {
				logger.error(
						e,
						"There are circular references clazz [{}] parameterName [{}] in [{}]",
						parameterDescriptors.getDeclaringClass(),
						parameterDescriptor.getName(),
						parameterDescriptors.getSource());
				return false;
			} finally {
				index++;
			}
		}
		return true;
	}

	protected abstract boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index);

	public Object[] getParameters(ParameterDescriptors parameterDescriptors) {
		Object[] args = new Object[parameterDescriptors.size()];
		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			try {
				args[index] = getParameter(parameterDescriptors, parameterDescriptor, index);
			} catch (Exception e) {
				throw new ParameterException(parameterDescriptors.getSource() + " parameter index " + index + " descriptor " + parameterDescriptor, e);
			}
			index++;
		}
		return args;
	}

	protected abstract Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception;
}
