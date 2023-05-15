package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import io.basc.framework.aop.Aop;
import io.basc.framework.aop.Proxy;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.AbstractMethodExecutor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodExecutor extends AbstractMethodExecutor {
	private final Supplier<? extends Object> targetSupplier;
	private Aop aop;

	public MethodExecutor(Supplier<? extends Object> targetSupplier, Method method) {
		super(method);
		Assert.requiredArgument(targetSupplier != null, "targetSupplier");
		this.targetSupplier = targetSupplier;
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
		Object source = getInstance(targetSupplier);
		Object bean = ReflectionUtils.invoke(getMethod(), source, args.map((e) -> e.getSource()).toArray());
		if (aop == null) {
			return bean;
		}

		Proxy proxy = aop.getProxy(getTypeDescriptor().getType(), bean);
		return proxy.create();
	}
}
