package io.basc.framework.execution;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public abstract class AbstractMethodExecutor extends AbstractExecutor {
	private final Method method;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public AbstractMethodExecutor(Method method) {
		Assert.requiredArgument(method != null, "method");
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public <T> T getInstance(Supplier<? extends T> supplier) {
		return Modifier.isStatic(method.getModifiers()) ? null : supplier.get();
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
}
