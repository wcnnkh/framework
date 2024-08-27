package io.basc.framework.execution;

import java.lang.reflect.Modifier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.execution.param.ParameterMatchingResults;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Name;
import io.basc.framework.util.function.Processor;

/**
 * 所有执行的基类
 * 
 * @author wcnnkh
 *
 */
public interface Executable extends Executed, Name {
	default boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		return getParameterDescriptors().map((e) -> e.getTypeDescriptor().getType()).equals(parameterTypes,
				Class::isAssignableFrom);
	}

	default boolean canExecuted(Parameters parameters) {
		ParameterMatchingResults results = parameters.apply(getParameterDescriptors());
		return results.isSuccessful();
	}

	default <T, E extends Throwable> T execute(Parameters parameters,
			Processor<? super ParameterMatchingResults, ? extends T, ? extends E> processor) throws E {
		ParameterMatchingResults results = parameters.apply(getParameterDescriptors());
		if (!results.isSuccessful()) {
			throw new IllegalArgumentException("Parameter mismatch");
		}

		return processor.process(results);
	}

	/**
	 * 执行需要的参数描述
	 * 
	 * @return
	 */
	Elements<ParameterDescriptor> getParameterDescriptors();

	/**
	 * 
	 * 异常类型
	 * 
	 * @return
	 */
	Elements<TypeDescriptor> getExceptionTypeDescriptors();

	/**
	 * 声明的类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getDeclaringTypeDescriptor();

	/**
	 * Returns the Java language modifiers for the member or constructor represented
	 * by this Member, as an integer. The Modifier class should be used to decode
	 * the modifiers in the integer.
	 *
	 * @return the Java language modifiers for the underlying member
	 * @see Modifier
	 */
	default int getModifiers() {
		return Modifier.PUBLIC;
	}
}
