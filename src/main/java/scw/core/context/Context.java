package scw.core.context;

public interface Context {
	Object getResource(Object name);

	Object bindResource(Object name, Object value);

	/**
	 * 监听上下文生命周期
	 * @param lifeCycle
	 */
	void lifeCycle(ContextLifeCycle lifeCycle);
}
