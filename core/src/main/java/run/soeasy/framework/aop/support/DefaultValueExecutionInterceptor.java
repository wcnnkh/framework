package run.soeasy.framework.aop.support;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import run.soeasy.framework.aop.ExecutionInterceptor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exe.Execution;
import run.soeasy.framework.core.exe.Executor;
import run.soeasy.framework.core.param.ParameterDescriptor;

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

	protected Source getDefaultReturnValue(Execution function) {
		return getDefaultValue(function, function.getReturnTypeDescriptor());
	}

	protected abstract Source getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
