package io.basc.framework.validation.aop;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Max;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionConstructor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.Elements;
import io.basc.framework.validation.FastValidator;
import lombok.NonNull;

public class ValidationExecutionInterceptor implements ExecutionInterceptor {
	private Validator validator;

	public ValidationExecutionInterceptor() {
		this(FastValidator.getValidator());
	}

	public ValidationExecutionInterceptor(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if(function instanceof ReflectionConstructor) {
			ReflectionConstructor reflectionConstructor = (ReflectionConstructor) function;
			validator.forExecutables().validateConstructorParameters(reflectionConstructor.getMember(), null, null)
		}
		
		if (!args.isEmpty()) {
			
		}
	}

	private Object execute(ReflectionMethod executor, Elements<Object> args) throws Throwable {
		if (!args.isEmpty()) {
			FastValidator.validate(() -> validator.forExecutables().validateParameters(executor.getTarget(),
					executor.getExecutable(), args.toArray()));
		}
		Object value = executor.execute(args);
		FastValidator.validate(() -> validator.forExecutables().validateReturnValue(executor.getTarget(),
				executor.getExecutable(), value));
		return value;
	}

	private Object execute(ReflectionConstructor executor, Elements<Object> args) throws Throwable {
		if (!args.isEmpty()) {
			FastValidator.validate(() -> validator.forExecutables()
					.validateConstructorParameters(executor.getExecutable(), args.toArray()));
		}
		Object value = executor.execute(args);
		FastValidator.validate(() -> validator.forExecutables().validateConstructorReturnValue(executor.getExecutable(),
				args.toArray()));
		return value;
	}

	protected Object execute(Executor executor, Elements<Object> args) throws Throwable {
		return executor.execute(args);
	}

	public Object intercept(Executor executor, Elements<Object> args) throws Throwable {
		if (executor instanceof ReflectionMethod) {
			return execute((ReflectionMethod) executor, args);
		} else if (executor instanceof ReflectionConstructor) {
			return execute((ReflectionConstructor) executor, args);
		} else {
			return execute(executor, args);
		}
	}

}
