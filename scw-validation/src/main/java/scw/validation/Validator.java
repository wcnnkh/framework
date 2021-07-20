package scw.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface Validator {
	<T> void validate(T instance, Class<?>... groups) throws ValidationException;

	<T> void validateProperty(T object, String propertyName, Class<?>... groups) throws ValidationException;

	<T> void validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups)
			throws ValidationException;

	<T> void validate(Class<T> clazz, Constructor<T> constructor, Object[] args, Class<?>... groups)
			throws ValidationException;

	<T> void validate(Class<T> clazz, Method method, Object[] args, Class<?>... groups) throws ValidationException;
}
