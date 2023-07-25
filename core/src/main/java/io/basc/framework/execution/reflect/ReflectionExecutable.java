package io.basc.framework.execution.reflect;

import java.lang.reflect.Executable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class ReflectionExecutable<T extends Executable> implements io.basc.framework.execution.Executable {
	private final TypeDescriptor source;
	private final T executable;
	private volatile String name;
	private volatile TypeDescriptor returnTypeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ReflectionExecutable(TypeDescriptor source, T executable) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(executable != null, "executable");
		this.source = source;
		this.executable = executable;
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
	public TypeDescriptor getReturnTypeDescriptor() {
		if (returnTypeDescriptor == null) {
			synchronized (this) {
				if (returnTypeDescriptor == null) {
					MethodParameter methodParameter = MethodParameter.forExecutable(executable, -1);
					this.returnTypeDescriptor = new TypeDescriptor(methodParameter);
				}
			}
		}
		return returnTypeDescriptor;
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
		return source.toString();
	}
}
