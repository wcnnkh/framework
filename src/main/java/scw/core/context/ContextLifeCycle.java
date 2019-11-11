package scw.core.context;

public interface ContextLifeCycle {
	/**
	 * 上下行初始化后调用
	 * @param context
	 */
	void after(Context context);
	
	/**
	 * 发生异常时
	 * @param context
	 *  @param e
	 */
	void error(Context context, Throwable e);
	
	/**
	 * 销毁时调用,此方法一定会调用
	 * 
	 * @param context
	 */
	void release(Context context);
}
