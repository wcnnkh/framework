package io.basc.framework.beans.support;

import java.lang.reflect.Constructor;

import io.basc.framework.beans.Executable;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutableConstructor implements Executable {
	private final Constructor<?> constructor;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ExecutableConstructor(Constructor<?> constructor) {
		Assert.requiredArgument(constructor != null, "constructor");
		this.constructor = constructor;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					MethodParameter methodParameter = new MethodParameter(constructor, -1);
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
					this.parameterDescriptors = ParameterUtils.getParameters(constructor);
				}
			}
		}
		return this.parameterDescriptors;
	}

	@Override
	public boolean isExecuted() {
		return constructor.getParameterCount() == 0;
	}

	@Override
	public Object execute() {
		return ReflectionUtils.newInstance(constructor);
	}

	@Override
	public boolean isExecutedByTypes(Elements<? extends TypeDescriptor> types) {
		return getParameterDescriptors().equals(types, (param, type) -> type.isAssignableTo(param.getTypeDescriptor()));
	}

	@Override
	public Object executeByTypes(Elements<? extends Value> args) {
		return ReflectionUtils.newInstance(constructor, args.map((e) -> e.getSource()).toArray());
	}

	@Override
	public boolean isExecuteByParameters(Elements<? extends Parameter> parameters) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object executeByParameters(Elements<? extends Parameter> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
