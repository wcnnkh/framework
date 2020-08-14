package scw.core.instance;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;

public class EnumerationMethodParameterDescriptors implements Enumeration<ParameterDescriptor[]> {
	private final Enumeration<Method> enumeration;

	public EnumerationMethodParameterDescriptors(Enumeration<Method> enumeration) {
		this.enumeration = enumeration;
	}

	/**
	 * @param targetClass
	 * @param method
	 * @param polymorphic 是否将多态的方法也包含在内
	 */
	public EnumerationMethodParameterDescriptors(Class<?> targetClass, final Method method, boolean polymorphic) {
		if (polymorphic) {
			this.enumeration = Collections.enumeration(ReflectionUtils.getMethodOrderList(targetClass, method));
		} else {
			this.enumeration = Collections.enumeration(Arrays.asList(method));
		}
	}

	public boolean hasMoreElements() {
		return enumeration.hasMoreElements();
	}

	public ParameterDescriptor[] nextElement() {
		Method method = enumeration.nextElement();
		return ParameterUtils.getParameterDescriptors(method);
	}

}
