package scw.record;

/**
 * 记录管理
 * @author asus1
 *
 * @param <R>
 */
public interface RecordManager<R> {
	/**
	 * 添加一条记录
	 * @param id
	 * @param record
	 * @return 如果记录已经存在返回false
	 */
	boolean add(String id, R record);
	
	/**
	 * 返回以add的顺序取第一个添加的
	 * @return
	 */
	String getFirst();
	
	/**
	 * 返回以add的顺序取取最后一个添加的
	 * @return
	 */
	String getLast();
	
	/**
	 * 删除一条记录
	 * @param id
	 * @return 如果记录不存在就返回false
	 */
	boolean remove(String id);
	
	/**
	 * 判断记录是否存在
	 * @param id
	 * @return
	 */
	boolean exists(String id);
	
	/**
	 * 获取一条记录
	 * @param id
	 * @return
	 */
	R get(String id);
	
	/**
	 * 插入一条记录，如果记录已经存在就更新
	 * @param id
	 * @param record
	 */
	void set(String id, R record);
}
