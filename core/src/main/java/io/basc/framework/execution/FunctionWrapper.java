package io.basc.framework.execution;

import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.execution.param.ParameterMatchingResults;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;

public class FunctionWrapper<W extends Function> extends Wrapper<W> implements Function {

	public FunctionWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public <T, E extends Throwable> T execute(Parameters parameters,
			Processor<? super ParameterMatchingResults, ? extends T, ? extends E> processor) throws E {
		return wrappedTarget.execute(parameters, processor);
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return wrappedTarget.getExceptionTypeDescriptors();
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return wrappedTarget.getReturnTypeDescriptor();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return wrappedTarget.getDeclaringTypeDescriptor();
	}

	@Override
	public Elements<ParameterDescriptor> getParameterDescriptors() {
		return wrappedTarget.getParameterDescriptors();
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return wrappedTarget.getAnnotations();
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return wrappedTarget.execute(args);
	}

	@Override
	public boolean canExecuted() {
		return wrappedTarget.canExecuted();
	}

	@Override
	public boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		return wrappedTarget.canExecuted(parameterTypes);
	}

	@Override
	public boolean canExecuted(Parameters parameters) {
		return wrappedTarget.canExecuted(parameters);
	}

	@Override
	public Object execute() throws Throwable {
		return wrappedTarget.execute();
	}

	@Override
	public Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable {
		return wrappedTarget.execute(parameterTypes, args);
	}

	@Override
	public Object execute(Parameters parameters) throws Throwable {
		return wrappedTarget.execute(parameters);
	}

	@Override
	public int getModifiers() {
		return wrappedTarget.getModifiers();
	}

	@Override
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return wrappedTarget.getAllAnnotationAttributes(annotationName);
	}

	@Override
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName,
			boolean classValuesAsString) {
		return wrappedTarget.getAllAnnotationAttributes(annotationName, classValuesAsString);
	}

	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationName) {
		return wrappedTarget.getAnnotationAttributes(annotationName);
	}

	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return wrappedTarget.getAnnotationAttributes(annotationName, classValuesAsString);
	}
}
