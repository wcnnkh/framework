package io.basc.framework.exec.aop.cglib;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.exec.AbstractExecutable;
import io.basc.framework.exec.ExecutionException;
import io.basc.framework.exec.ExecutionInterceptor;
import io.basc.framework.exec.reflect.ConstructorExecutors;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.Enhancer;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyExecutable extends AbstractExecutable {
	private final ConstructorExecutors executors;
	private final Enhancer enhancer;

	public CglibProxyExecutable(TypeDescriptor typeDescriptor, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
		this(new ConstructorExecutors(typeDescriptor), interfaces, executionInterceptor);
	}

	public CglibProxyExecutable(ConstructorExecutors executors, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
		this.executors = executors;
		this.enhancer = CglibUtils.createEnhancer(executors.getTypeDescriptor().getType(), interfaces);
		this.enhancer.setCallback(new ExecutionInterceptorToMethodInterceptor(this, executionInterceptor));
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return executors.getTypeDescriptor();
	}

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		return executors.isExecutable(types);
	}

	@Override
	public Object execute(Elements<? extends Value> args) throws ExecutionException {
		try {
			return enhancer.create(args.map((e) -> e.getTypeDescriptor().getType()).toArray(new Class[0]),
					args.map((e) -> e.getSource()).toArray());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(args.toString(), e);
		}
	}
}