package scw.beans;

public class DefaultBeanLifeCycle implements BeanLifeCycle, Init, Destroy {
	private volatile boolean initialized = false;
	private BeanLifeCycle synchronization;

	public BeanLifeCycle getSynchronization() {
		return synchronization;
	}

	public void setSynchronization(BeanLifeCycle synchronization) {
		this.synchronization = synchronization;
	}

	public void init() throws Throwable {
		if (initialized) {
			throw new BeansException("Already initialized");
		}

		synchronized (this) {
			if (initialized) {
				throw new BeansException("Already initialized");
			}

			try {
				beforeInit();
				initInternal();
				afterInit();
			} finally {
				initComplete();
			}
			initialized = true;
		}
	}

	protected void initInternal() throws Throwable {
		if (synchronization != null) {
			if (synchronization instanceof DefaultBeanLifeCycle) {
				DefaultBeanLifeCycle beanLifeCycle = (DefaultBeanLifeCycle) synchronization;
				if (!beanLifeCycle.isInitialized()) {
					beanLifeCycle.init();
				}
			}
		}
	}

	public void destroy() throws Throwable {
		if (!initialized) {
			throw new BeansException("Not yet initialized");
		}

		synchronized (this) {
			if (!initialized) {
				throw new BeansException("Not yet initialized");
			}

			try {
				beforeDestroy();
				destroyInternal();
				afterDestroy();
			} finally {
				destroyComplete();
			}
			initialized = false;
		}
	}

	protected void destroyInternal() throws Throwable {
		if (synchronization != null) {
			if (synchronization instanceof DefaultBeanLifeCycle) {
				DefaultBeanLifeCycle beanLifeCycle = (DefaultBeanLifeCycle) synchronization;
				if (beanLifeCycle.isInitialized()) {
					beanLifeCycle.destroy();
				}
			}
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void beforeInit() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.beforeInit();
		}
	}

	public void afterInit() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.afterInit();
		}
	}

	public void initComplete() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.initComplete();
		}
	}

	public void beforeDestroy() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.beforeDestroy();
		}
	}

	public void afterDestroy() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.afterDestroy();
		}
	}

	public void destroyComplete() throws Throwable {
		if (synchronization != null && !(synchronization instanceof DefaultBeanLifeCycle)) {
			synchronization.destroyComplete();
		}
	}

}
