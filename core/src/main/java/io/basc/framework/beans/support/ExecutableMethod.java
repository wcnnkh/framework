package io.basc.framework.beans.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import io.basc.framework.beans.Executable;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Data;

@Data
public class ExecutableMethod implements Executable {
	private final Supplier<? extends Object> targetSupplier;
	private final Method method;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ExecutableMethod(Supplier<? extends Object> targetSupplier, Method method) {
		Assert.requiredArgument(targetSupplier != null, "targetSupplier");
		Assert.requiredArgument(method != null, "method");
		this.targetSupplier = targetSupplier;
		this.method = method;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					this.typeDescriptor = TypeDescriptor.forMethodReturnType(method);
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
					this.parameterDescriptors = ParameterUtils.getParameters(method);
				}
			}
		}
		return this.parameterDescriptors;
	}

	@Override
	public boolean isExecuted() {
		return method.getParameterCount() == 0;
	}

	@Override
	public Object execute() {
		Object target = Modifier.isStatic(method.getModifiers()) ? null : targetSupplier.get();
		return ReflectionUtils.invoke(method, target);
	}

	@Override
	public boolean isExecutedByTypes(Elements<? extends TypeDescriptor> types) {
		return getParameterDescriptors().equals(types, (param, type) -> type.isAssignableTo(param.getTypeDescriptor()));
	}

	@Override
	public Object executeByTypes(Elements<? extends Value> args) {
		Object source = Modifier.isStatic(method.getModifiers()) ? null : targetSupplier.get();
		return ReflectionUtils.invoke(method, source, args.map((e) -> e.getSource()).toArray());
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
