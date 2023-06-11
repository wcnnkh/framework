package io.basc.framework.execution.reflect;

import java.lang.reflect.Executable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.Getter;

@Getter
public abstract class ExecutableReflection<T extends Executable> implements io.basc.framework.execution.Executable {
	private final T source;
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ExecutableReflection(T source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					this.name = source.getName();
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
					MethodParameter methodParameter = MethodParameter.forExecutable(source, -1);
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
					this.parameterDescriptors = ParameterUtils.getParameters(source);
				}
			}
		}
		return parameterDescriptors;
	}

	@Override
	public String toString() {
		return source.toString();
	}
}
