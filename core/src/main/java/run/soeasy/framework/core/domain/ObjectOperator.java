package run.soeasy.framework.core.domain;

/**
 * 对象操作
 * 
 * @author soeasy.run
 *
 * @param <T>
 */
public interface ObjectOperator<T> {
	/**
	 * 创建
	 * 
	 * @return
	 */
	T create();

	/**
	 * 返回对外展示的
	 * 
	 * @param source
	 * @return
	 */
	T display(T source);

	/**
	 * 克隆
	 * 
	 * @param source
	 * @return
	 */
	T clone(T source);
}
