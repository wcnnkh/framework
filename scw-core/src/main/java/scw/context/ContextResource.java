package scw.context;

/**
 * 对应生命周期中的事件
 * @author shuchaowen
 *
 */
public interface ContextResource {
	void after();
	
	void error(Throwable e);

	void release();
}
