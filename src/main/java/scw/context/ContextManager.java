package scw.context;

public interface ContextManager {
	/**
	 * 创建一个上下文
	 * @return
	 */
	Context createContext();
	
	/**
	 * 获取当前上下文
	 * @return
	 */
	Context getCurrentContext();

	/**
	 * 回收一个上下文
	 * @param context
	 */
	void release(Context context);
}