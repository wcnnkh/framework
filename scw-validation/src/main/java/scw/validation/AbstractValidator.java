package scw.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import scw.core.parameter.ParameterUtils;

public abstract class AbstractValidator implements Validator {

	@Override
	public <T> void validate(Class<T> clazz, Constructor<T> constructor, Object[] args, Class<?>... groups)
			throws ValidationException {
		String[] names = ParameterUtils.getParameterNames(constructor);
		for (int i = 0; i < names.length; i++) {
			validateValue(clazz, names[i], args[i], groups);
		}
	}

	@Override
	public <T> void validate(Class<T> clazz, Method method, Object[] args, Class<?>... groups)
			throws ValidationException {
		String[] names = ParameterUtils.getParameterNames(method);
		for (int i = 0; i < names.length; i++) {
			validateValue(clazz, names[i], args[i], groups);
		}
	}

}
