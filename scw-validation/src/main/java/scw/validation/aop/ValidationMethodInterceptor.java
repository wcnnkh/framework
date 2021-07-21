package scw.validation.aop;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Provider;
import scw.core.reflect.MethodInvoker;
import scw.validation.FastValidator;
import scw.validation.ValidationUtils;

@Provider
public class ValidationMethodInterceptor implements MethodInterceptor {
	private Validator validator;

	public ValidationMethodInterceptor() {
		this(FastValidator.getValidator());
	}

	public ValidationMethodInterceptor(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		Set<ConstraintViolation<Object>> constraintViolations = validator.forExecutables()
				.validateParameters(invoker.getInstance(), invoker.getMethod(), args);
		ValidationUtils.throwValidationException(constraintViolations);
		Object returnValue = invoker.invoke(args);
		constraintViolations = validator.forExecutables().validateReturnValue(invoker.getInstance(),
				invoker.getMethod(), returnValue);
		ValidationUtils.throwValidationException(constraintViolations);
		return returnValue;
	}
}
