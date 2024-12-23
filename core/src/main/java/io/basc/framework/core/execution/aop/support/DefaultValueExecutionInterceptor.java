package io.basc.framework.core.execution.aop.support;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServiceAware;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 对调用参数默认值的处理
 * 
 * @author wcnnkh
 *
 */
@Data
@NoArgsConstructor
public abstract class DefaultValueExecutionInterceptor implements ExecutionInterceptor, ConversionServiceAware {
	private ConversionService conversionService;

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		if (args.length == 0) {
			return function.execute(args);
		}

		Elements<Object> newArgs = function.getParameterDescriptors().parallel(Elements.forArray(args))
				.filter((e) -> e.isPresent()).map((e) -> {
					if (e.getRightValue() != null) {
						return e.getRightValue();
					} else {
						return getDefaultParameterValue(function, e.getLeftValue());
					}
				});
		Object returnValue = function.execute(newArgs);
		if (returnValue == null) {
			Value defaultValue = getDefaultReturnValue(function);
			if (defaultValue != null) {
				returnValue = conversionService.convert(defaultValue, function.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected Value getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor) {
		return getDefaultValue(executor, parameterDescriptor.getTypeDescriptor());
	}

	protected Value getDefaultReturnValue(Function function) {
		return getDefaultValue(function, function.getReturnTypeDescriptor());
	}

	protected abstract Value getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
