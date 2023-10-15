package io.basc.framework.validation.aop;

import javax.validation.Validator;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.ConditionalOnMissingBean;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Order;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionConstructor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.validation.FastValidator;

@Component
@ConditionalOnMissingBean(ValidationMethodInterceptor.class)
@Order(Ordered.LOWEST_PRECEDENCE)
public class ValidationMethodInterceptor implements ExecutionInterceptor {
	private Validator validator;

	public ValidationMethodInterceptor() {
		this(FastValidator.getValidator());
	}

	public ValidationMethodInterceptor(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			ReflectionMethodExecutor methodExecutor = (ReflectionMethodExecutor) executor;
			FastValidator.validate(() -> validator.forExecutables().validateParameters(methodExecutor.getTarget(),
					methodExecutor.getExecutable(), args.toArray()));
			Object returnValue = executor.execute(args);
			FastValidator.validate(() -> validator.forExecutables().validateReturnValue(methodExecutor.getTarget(),
					methodExecutor.getExecutable(), returnValue));
			return returnValue;
		} else if (executor instanceof ReflectionConstructor) {
			ReflectionConstructor constructorExecutor = (ReflectionConstructor) executor;
			FastValidator.validate(() -> validator.forExecutables()
					.validateConstructorParameters(constructorExecutor.getExecutable(), args.toArray()));
			Object returnValue = executor.execute(args);
			FastValidator.validate(() -> validator.forExecutables()
					.validateConstructorReturnValue(constructorExecutor.getExecutable(), returnValue));
			return returnValue;
		} else {
			// TODO 还需要校验不
			return executor.execute(args);
		}
	}
}
