package scw.core.reflect;

import java.util.concurrent.Callable;

public class InvokerCallable implements Callable<Object> {
	private final Invoker invoker;
	private final Object[] args;

	public InvokerCallable(Invoker invoker, Object[] args) {
		this.invoker = invoker;
		this.args = args;
	}

	public final Invoker getInvoker() {
		return invoker;
	}

	public Object[] getArgs() {
		return args.clone();
	}

	public Object call() throws Exception {
		try {
			return invoker.invoke(args);
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
		}
		throw new IllegalStateException("Should never get here");
	}
}
