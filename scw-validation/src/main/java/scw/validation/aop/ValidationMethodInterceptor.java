package scw.validation.aop;

import javax.validation.Validator;

import scw.aop.MethodInterceptor;
import scw.context.annotation.Provider;
import scw.core.reflect.MethodInvoker;
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
		FastValidator.validate(() -> validator.forExecutables()
				.validateParameters(invoker.getInstance(), invoker.getMethod(), args));
		Object returnValue = invoker.invoke(args);
		FastValidator.validate(() -> validator.forExecutables().validateReturnValue(invoker.getInstance(),
				invoker.getMethod(), returnValue));
		return returnValue;
	}
}
