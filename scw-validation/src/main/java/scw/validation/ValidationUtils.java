package scw.validation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.CollectionUtils;

public class ValidationUtils {
	private ValidationUtils() {
	};

	public static <T> Set<ConstraintViolation<T>> validateByMethodParameter(Validator validator, Class<T> clazz,
			Method method, Object[] args) {
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameterDescriptors(method);
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			if (parameterDescriptor.isAnnotationPresent(Valid.class)) {
				Set<ConstraintViolation<T>> violations = validator.validateValue(clazz, parameterDescriptor.getName(),
						args[i]);
				if (CollectionUtils.isEmpty(violations)) {
					continue;
				}

				return violations;
			}
		}
		return Collections.emptySet();
	}
}
