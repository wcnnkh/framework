package io.basc.framework.exec.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.aop.Aop;
import io.basc.framework.aop.Proxy;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.exec.AbstractExecutor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConstructorExecutor extends AbstractExecutor {
	private Aop aop;
	private final Constructor<?> constructor;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public ConstructorExecutor(Constructor<?> constructor) {
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
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			return false;
		}
		return super.isExecutable(types);
	}

	@Override
	public Object execute(Elements<? extends Value> args) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			throw new UnsupportedException(constructor.toString());
		}

		Proxy proxy = aop.getProxy(getTypeDescriptor().getType());
		return proxy.create(args.map((e) -> e.getTypeDescriptor().getType()).toArray(new Class[0]),
				args.map((e) -> e.getSource()).toArray());
	}
}
