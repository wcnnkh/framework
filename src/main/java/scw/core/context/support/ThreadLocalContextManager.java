package scw.core.context.support;

import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.DefaultContext;
import scw.core.context.DefaultNestingContext;
import scw.core.context.NestingContext;

public final class ThreadLocalContextManager implements ContextManager {
	private ThreadLocal<DefaultContext> local = new ThreadLocal<DefaultContext>();
	private boolean nesting;

	public ThreadLocalContextManager(boolean nesting) {
		this.nesting = nesting;
	}

	public void release(Context context) {
		if (context == null) {
			throw new NullPointerException("release context不能为空");
		}

		if (!(context instanceof DefaultContext)) {
			throw new NullPointerException("上下文类型错误");
		}

		DefaultContext current = local.get();
		if (current != local.get()) {
			throw new RuntimeException("上下文必须按顺序销毁");
		}

		try {
			current.release();
		} finally {
			if (current instanceof NestingContext) {
				NestingContext nestingContext = (NestingContext) current;
				DefaultContext parent = (DefaultContext) nestingContext
						.getParentContext();
				if (parent == null) {
					local.remove();
				}
				local.set(parent);
			} else {
				local.remove();
			}
		}
	}

	public Context createContext() {
		DefaultContext context;
		if (nesting) {
			context = new DefaultNestingContext((NestingContext) local.get(),
					true);
		} else {
			context = new DefaultContext();
		}
		local.set(context);
		return context;
	}

	public Context getCurrentContext() {
		return local.get();
	}

}