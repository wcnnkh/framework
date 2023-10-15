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
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnMissingBean(ValidationExecutionInterceptor.class)
public class ValidationExecutionInterceptor implements ExecutionInterceptor {
	private Validator validator;

	public ValidationExecutionInterceptor() {
		this(FastValidator.getValidator());
	}

	public ValidationExecutionInterceptor(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			return execute((ReflectionMethodExecutor) executor, args);
		} else if (executor instanceof ReflectionConstructor) {
			return execute((ReflectionConstructor) executor, args);
		} else {
			return execute(executor, args);
		}
	}

	private Object execute(ReflectionMethodExecutor executor, Elements<? extends Object> args) throws Throwable {
		if (!args.isEmpty()) {
			FastValidator.validate(() -> validator.forExecutables().validateParameters(executor.getTarget(),
					executor.getExecutable(), args.toArray()));
		}
		Object value = executor.execute(args);
		FastValidator.validate(() -> validator.forExecutables().validateReturnValue(executor.getTarget(),
				executor.getExecutable(), value));
		return value;
	}

	private Object execute(ReflectionConstructor executor, Elements<? extends Object> args) throws Throwable {
		if (!args.isEmpty()) {
			FastValidator.validate(() -> validator.forExecutables()
					.validateConstructorParameters(executor.getExecutable(), args.toArray()));
		}
		Object value = executor.execute(args);
		FastValidator.validate(() -> validator.forExecutables().validateConstructorReturnValue(executor.getExecutable(),
				args.toArray()));
		return value;
	}

	protected Object execute(Executor executor, Elements<? extends Object> args) throws Throwable {
		return executor.execute(args);
	}
}