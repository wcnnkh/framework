package scw.context;

public interface ContextManager<T extends Context> {
	<V> V execute(Propagation propagation, ContextExecute<V> contextExecute) throws Throwable;

	T getContext();
	
	void addContextLifeCycle(ContextLifeCycle contextLifeCycle);
}
