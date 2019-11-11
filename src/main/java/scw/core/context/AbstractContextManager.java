package scw.core.context;

public abstract class AbstractContextManager<T extends Context> implements ContextManager<T>, ContextLifeCycle {

	public <V> V execute(Propagation propagation, ContextExecute<V> contextExecute) throws Throwable {
		T context = getContext(propagation);
		V value;
		try {
			value = contextExecute.execute(context);
			after(context);
			return value;
		} catch (Throwable e) {
			error(context, e);
			throw e;
		} finally {
			release(context);
		}
	}

	public void addContextLifeCycle(ContextLifeCycle contextLifeCycle) {
		Context context = getContext();
		if (context == null) {
			return;
		}

		context.addContextLifeCycle(contextLifeCycle);
	}

	public abstract T getContext(Propagation propagation) throws Throwable;

	public void after(Context context) {
		if (context instanceof ContextResource) {
			((ContextResource) context).after();
		}
	}

	public void error(Context context, Throwable e) {
		if (context instanceof ContextResource) {
			((ContextResource) context).error(e);
		}
	}

	public void release(Context context) {
		if (context instanceof ContextResource) {
			((ContextResource) context).release();
		}
	}
}
