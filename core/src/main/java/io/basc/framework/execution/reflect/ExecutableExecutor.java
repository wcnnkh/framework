package io.basc.framework.execution.reflect;

import java.lang.reflect.Executable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public abstract class ExecutableExecutor<T extends Executable> implements Executor {
	private final T executable;
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ExecutableExecutor(T executable) {
		Assert.requiredArgument(executable != null, "executable");
		this.executable = executable;
	}

	public T getExecutable() {
		return executable;
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					this.name = executable.getName();
				}
			}
		}
		return name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					MethodParameter methodParameter = MethodParameter.forExecutable(executable, -1);
					this.typeDescriptor = new TypeDescriptor(methodParameter);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					this.parameterDescriptors = ParameterUtils.getParameters(executable);
				}
			}
		}
		return parameterDescriptors;
	}

	@Override
	public String toString() {
		return executable.toString();
	}
}
