package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

/**
 * 可被拦截的执行器
 * 
 * @author wcnnkh
 *
 */
public class InterceptableExecutor implements Executor {
	private final Iterable<? extends ExecutionInterceptor> interceptors;
	private final Executable source;
	private final Executor executor;

	public InterceptableExecutor(Executable source, Iterable<? extends ExecutionInterceptor> interceptors,
			Executor executor) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(interceptors != null, "interceptors");
		Assert.requiredArgument(executor != null, "executor");
		this.source = source;
		this.interceptors = interceptors;
		this.executor = executor;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return executor.getTypeDescriptor();
	}

	@Override
	public boolean isExecutable() {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.interceptors.iterator(),
				this.executor);
		return chain.isExecutable();
	}

	@Override
	public Object execute() throws ExecutionException {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.interceptors.iterator(),
				this.executor);
		return chain.execute();
	}

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.interceptors.iterator(),
				this.executor);
		return chain.isExecutable(types);
	}

	@Override
	public Object execute(Elements<? extends Value> args) throws ExecutionException {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.interceptors.iterator(),
				this.executor);
		return chain.execute(args);
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return this.executor.getParameterDescriptors();
	}
}
