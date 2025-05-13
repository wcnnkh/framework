package run.soeasy.framework.aop.support;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.invoke.Execution;
import run.soeasy.framework.core.invoke.Executor;
import run.soeasy.framework.core.invoke.intercept.ExecutionInterceptor;
import run.soeasy.framework.core.transform.indexed.IndexedDescriptor;

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
	public Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable {
		if (args.length == 0) {
			return function.execute(args);
		}

		Elements<Object> newArgs = function.getParameterTemplate().parallel(Elements.forArray(args))
				.filter((e) -> e.isPresent()).map((e) -> {
					if (e.getRightValue() != null) {
						return e.getRightValue();
					} else {
						return getDefaultParameterValue(function, e.getLeftValue());
					}
				});
		Object returnValue = function.execute(newArgs);
		if (returnValue == null) {
			TypedValue defaultValue = getDefaultReturnValue(function);
			if (defaultValue != null) {
				returnValue = conversionService.convert(defaultValue, function.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected TypedValue getDefaultParameterValue(Executor executor, IndexedDescriptor parameterDescriptor) {
		return getDefaultValue(executor, parameterDescriptor.getReturnTypeDescriptor());
	}

	protected TypedValue getDefaultReturnValue(Execution function) {
		return getDefaultValue(function, function.getReturnTypeDescriptor());
	}

	protected abstract TypedValue getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
