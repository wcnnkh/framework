package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

public interface Executed extends AnnotatedTypeMetadata {
	/**
	 * 返回类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getReturnTypeDescriptor();

	default boolean canExecuted() {
		return canExecuted(Elements.empty());
	}

	boolean canExecuted(Elements<? extends Class<?>> parameterTypes);

	default boolean canExecuted(Parameters parameters) {
		return canExecuted(parameters.getTypes());
	}
}
