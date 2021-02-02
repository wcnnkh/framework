package scw.context.support;

import java.util.concurrent.atomic.AtomicBoolean;

import scw.context.Destroy;
import scw.context.Init;

public class LifecycleAuxiliary implements Init, Destroy{
	private final AtomicBoolean initializing = new AtomicBoolean(false);
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	/**
	 * 初始化
	 */
	public void init() throws Throwable {
		if (isInitialized() || isInitializing()) {
			throw new RuntimeException("Already initialized");
		}

		if (initializing.compareAndSet(false, true)) {
			try {
				beforeInit();
				afterInit();
			} finally {
				try {
					initComplete();
				} catch (Exception e) {
					initialized.set(true);
				}
			}
		} else {
			throw new RuntimeException("Already initialized");
		}
	}

	/**
	 * 销毁
	 */
	public void destroy() throws Throwable {
		if (!isInitializing() && !isInitialized()) {
			throw new RuntimeException("Not yet initialized");
		}

		if (initializing.compareAndSet(true, false)) {
			try {
				beforeDestroy();
				afterDestroy();
			} finally {
				try {
					destroyComplete();
				} finally {
					initialized.set(false);
				}
			}
		} else {
			throw new RuntimeException("Not yet initialized");
		}
	}

	/**
	 * 是否已经初始化完毕
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return initialized.get();
	}

	public boolean isInitializing() {
		return initializing.get();
	}

	/**
	 * 初始化之前执行
	 * @throws Throwable
	 */
	protected void beforeInit() throws Throwable {
	}

	/**
	 * 初始化之后执行
	 * @throws Throwable
	 */
	protected void afterInit() throws Throwable {
	}

	/**
	 * 初始化完成后
	 * @throws Throwable
	 */
	protected void initComplete() throws Throwable {
	}

	/**
	 * 销毁之前执行
	 * @throws Throwable
	 */
	protected void beforeDestroy() throws Throwable {
	}

	/**
	 * 销毁之后执行
	 * @throws Throwable
	 */
	protected void afterDestroy() throws Throwable {
	}

	/**
	 * 销毁完成后执行
	 * @throws Throwable
	 */
	protected void destroyComplete() throws Throwable {
	}
	
	public static void init(Object init) throws Throwable {
		if (init == null) {
			return;
		}

		if (init instanceof Init) {
			((Init) init).init();
		}
	}

	public static void destroy(Object destroy) throws Throwable {
		if (destroy == null) {
			return;
		}

		if (destroy instanceof Destroy) {
			((Destroy) destroy).destroy();
		}
	}
}
