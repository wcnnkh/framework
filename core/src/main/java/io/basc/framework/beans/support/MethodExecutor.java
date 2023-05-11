package io.basc.framework.beans.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import io.basc.framework.aop.Aop;
import io.basc.framework.aop.Proxy;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodExecutor extends AbstractBeanExecutor {
	private final Supplier<? extends Object> targetSupplier;
	private final Method method;
	private volatile TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;
	private Aop aop;

	public MethodExecutor(Supplier<? extends Object> targetSupplier, Method method) {
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
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			return false;
		}
		return super.isExecutable(types);
	}

	@Override
	public Object execute(Elements<? extends Value> args) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			throw new UnsupportedException("Proxy " + getTypeDescriptor().getType());
		}
		Object source = Modifier.isStatic(method.getModifiers()) ? null : targetSupplier.get();
		Object bean = ReflectionUtils.invoke(method, source, args.map((e) -> e.getSource()).toArray());
		if (aop == null) {
			return bean;
		}

		Proxy proxy = aop.getProxy(getTypeDescriptor().getType(), bean);
		return proxy.create();
	}

	@Override
	public boolean isExecutableByParameters(Elements<? extends Value> parameters) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			return false;
		}
		return super.isExecutableByParameters(parameters);
	}

	@Override
	public Object executeByParameters(Elements<? extends Value> parameters) {
		if (aop != null && !aop.canProxy(getTypeDescriptor().getType())) {
			throw new UnsupportedException(method.toString());
		}
		return super.executeByParameters(parameters);
	}
}
