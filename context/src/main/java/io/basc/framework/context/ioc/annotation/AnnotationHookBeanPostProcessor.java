package io.basc.framework.context.ioc.annotation;

import io.basc.framework.beans.factory.config.support.MethodHookBeanPostProcessor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;

class AnnotationHookBeanPostProcessor extends MethodHookBeanPostProcessor {
	public AnnotationHookBeanPostProcessor(ExecutionParametersExtractor executionParametersExtractor) {
		super(executionParametersExtractor);
	}

	@Override
	protected boolean isInitializeExecutor(Executor executor) {
		return executor.getReturnType().hasAnnotation(InitMethod.class);
	}

	@Override
	protected boolean isDestoryExecutor(Executor executor) {
		return executor.getReturnType().hasAnnotation(Destroy.class);
	}

}
