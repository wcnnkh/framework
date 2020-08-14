package scw.core.instance;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Enumeration;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;

public class EnumerationConstructorParameterDescriptors implements Enumeration<ParameterDescriptor[]> {
	private final Enumeration<Constructor<?>> enumeration;

	public EnumerationConstructorParameterDescriptors(Enumeration<Constructor<?>> enumeration) {
		this.enumeration = enumeration;
	}

	public EnumerationConstructorParameterDescriptors(Class<?> clazz) {
		this(Collections.enumeration(ReflectionUtils.getConstructorOrderList(clazz)));
	}

	public boolean hasMoreElements() {
		return enumeration.hasMoreElements();
	}

	public ParameterDescriptor[] nextElement() {
		Constructor<?> constructor = enumeration.nextElement();
		return ParameterUtils.getParameterDescriptors(constructor);
	}

}
