package io.basc.framework.execution.reflect;

import java.lang.reflect.Executable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "executable")
@ToString(of = "executable")
public abstract class ReflectionExecutor<T extends Executable> implements Executor {
	private final T executable;
	private volatile TypeDescriptor returnTypeDescriptor;
	private volatile ParameterDescriptor[] parameterDescriptors;
	private Elements<Parameter> parameters;

	public ReflectionExecutor(T executable) {
		Assert.requiredArgument(executable != null, "executable");
		this.executable = executable;
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
	public Elements<ParameterDescriptor> getParameterDescriptors() {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					this.parameterDescriptors = ParameterUtils.getParameters(executable);
				}
			}
		}
		return Elements.forArray(parameterDescriptors);
	}

	public void setParameters(Elements<Parameter> parameters) {
		Assert.requiredArgument(parameters != null, "parameters");
		this.parameters = parameters;
	}
}
