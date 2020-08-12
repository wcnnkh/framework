package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import scw.core.LocalVariableTableParameterNameDiscoverer;
import scw.lang.RequiredJavaVersion;

@RequiredJavaVersion(8)
public class Jdk8ParameterNameDiscoverer extends LocalVariableTableParameterNameDiscoverer {
	@Override
	public String[] getParameterNames(Constructor<?> ctor) {
		String[] names = super.getParameterNames(ctor);
		if ((names == null && ctor.getParameterCount() != 0)
				|| (names != null && names.length != ctor.getParameterCount())) {
			Parameter[] parameters = ctor.getParameters();
			names = new String[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				names[i] = parameters[i].getName();
			}
		}
		return names;
	}

	@Override
	public String[] getParameterNames(Method method) {
		String[] names = super.getParameterNames(method);
		if ((names == null && method.getParameterCount() != 0)
				|| (names != null && names.length != method.getParameterCount())) {
			Parameter[] parameters = method.getParameters();
			names = new String[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				names[i] = parameters[i].getName();
			}
		}
		return names;
	}
}
