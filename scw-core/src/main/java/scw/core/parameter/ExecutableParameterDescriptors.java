package scw.core.parameter;

import java.lang.reflect.Executable;

public class ExecutableParameterDescriptors extends DefaultParameterDescriptors<Executable> {

	public ExecutableParameterDescriptors(Class<?> targetClass, Executable executable) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass, executable);
	}

	public ExecutableParameterDescriptors(ParameterNameDiscoverer parameterNameDiscoverer, Class<?> targetClass,
			Executable executable) {
		super(executable, targetClass, ParameterUtils.getParameters(parameterNameDiscoverer, executable));
	}
}
