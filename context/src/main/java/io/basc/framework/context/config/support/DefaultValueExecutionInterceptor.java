package io.basc.framework.context.config.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (args.isEmpty()) {
			return executor.execute(args);
		}

		List<Object> newArgs = new ArrayList<>(8);
		Iterator<? extends Object> argIterator = args.iterator();
		Iterator<? extends ParameterDescriptor> descriptorIterator = executor.getParameterDescriptors().iterator();
		while (argIterator.hasNext() && descriptorIterator.hasNext()) {
			Object arg = argIterator.next();
			ParameterDescriptor parameterDescriptor = descriptorIterator.next();
			if (arg == null) {
				arg = getDefaultParameterValue(executor, parameterDescriptor);
				if (arg != null) {
					arg = conversionService.convert(arg, parameterDescriptor.getTypeDescriptor());
				}
			}
			newArgs.add(arg);
		}

		Object returnValue = executor.execute(Elements.of(newArgs));
		if (returnValue == null) {
			returnValue = getDefaultReturnValue(executor);
			if (returnValue != null) {
				returnValue = conversionService.convert(returnValue, executor.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected abstract Object getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor);

	protected abstract Object getDefaultReturnValue(Executor executor);
}
