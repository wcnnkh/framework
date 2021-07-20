package scw.validation.aop;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Provider;
import scw.core.reflect.MethodInvoker;
import scw.validation.ValidationUtils;
import scw.validation.Validator;

@Provider
public class ValidationMethodInterceptor implements MethodInterceptor {
	private final Validator validator;

	public ValidationMethodInterceptor() {
		this(ValidationUtils.getValidator());
	}

	public ValidationMethodInterceptor(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		validator.validate(invoker.getDeclaringClass(), invoker.getMethod(), args);
		return invoker.invoke(args);
	}
}
