package scw.validation.aop;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.context.annotation.Provider;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.MethodInvoker;
import scw.validation.ValidationUtils;

@Provider
public class ValidationMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private final ValidatorFactory validatorFactory;
	
	public ValidationMethodInterceptor(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}
	
	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		Validator validator = validatorFactory.getValidator();
		Set<?> a = ValidationUtils.validateByMethodParameter(validator, null, null, args);
		return invoker.invoke(args);
	}

	@Override
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		for(ParameterDescriptor parameterDescriptor : ParameterUtils.getParameterDescriptors(invoker.getMethod())) {
			if(parameterDescriptor.isAnnotationPresent(Valid.class)) {
				return true;
			}
		}
		return false;
	}

}
