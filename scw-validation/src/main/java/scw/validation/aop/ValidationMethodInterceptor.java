package scw.validation.aop;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Provider;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.CollectionUtils;
import scw.validation.FastValidator;

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
		if(!CollectionUtils.isEmpty(constraintViolations)) {
			throw new ConstraintViolationException(constraintViolations);
		}
		
		Object returnValue = invoker.invoke(args);
		
		constraintViolations = validator.forExecutables().validateReturnValue(invoker.getInstance(),
				invoker.getMethod(), returnValue);
		if(!CollectionUtils.isEmpty(constraintViolations)) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return returnValue;
	}
}
