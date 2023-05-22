package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

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
	public Object execute(Elements<? extends Object> args) throws ExecutionException {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.interceptors.iterator(),
				this.executor);
		return chain.execute(args);
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return this.executor.getParameterDescriptors();
	}

	@Override
	public String getName() {
		return executor.getName();
	}
}
