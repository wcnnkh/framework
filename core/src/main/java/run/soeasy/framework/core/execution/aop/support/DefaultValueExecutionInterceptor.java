package run.soeasy.framework.core.execution.aop.support;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.execution.Executor;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.ParameterDescriptor;
import run.soeasy.framework.core.execution.aop.ExecutionInterceptor;
import run.soeasy.framework.util.collections.Elements;

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
			Source defaultValue = getDefaultReturnValue(function);
			if (defaultValue != null) {
				returnValue = conversionService.convert(defaultValue, function.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected Source getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor) {
		return getDefaultValue(executor, parameterDescriptor.getTypeDescriptor());
	}

	protected Source getDefaultReturnValue(Function function) {
		return getDefaultValue(function, function.getReturnTypeDescriptor());
	}

	protected abstract Source getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
