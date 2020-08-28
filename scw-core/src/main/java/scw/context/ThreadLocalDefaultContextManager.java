package scw.context;

public class ThreadLocalDefaultContextManager extends DefaultContextManager {
	private final ThreadLocal<DefaultContext> threadLocal = new ThreadLocal<DefaultContext>();

	public DefaultContext getContext() {
		return threadLocal.get();
	}

	@Override
	public void setContext(DefaultContext defaultContext) {
		threadLocal.set(defaultContext);
	}

	@Override
	public void removeContext() {
		threadLocal.remove();
	}

}
