package io.basc.framework.validation.aop;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.validation.FastValidator;

import javax.validation.Validator;

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
		FastValidator.validate(() -> validator.forExecutables()
				.validateParameters(invoker.getInstance(), invoker.getMethod(), args));
		Object returnValue = invoker.invoke(args);
		FastValidator.validate(() -> validator.forExecutables().validateReturnValue(invoker.getInstance(),
				invoker.getMethod(), returnValue));
		return returnValue;
	}
}
