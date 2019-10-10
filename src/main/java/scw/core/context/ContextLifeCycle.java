package scw.core.context;

public interface ContextLifeCycle {
	/**
	 * 销毁时调用
	 */
	void release();
}
