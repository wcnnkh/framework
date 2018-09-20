package shuchaowen.core.db.proxy;

import java.io.Serializable;
import java.util.Map;

/**
 * 获取一个数据库实体类字段值的变化
 * 方法名取的很怪的原因是为了防止被重写
 * @author shuchaowen
 *
 */
public interface BeanProxy extends Serializable{
	public static final String GET_CHANGE_COLUMN_MAP = "getChange_ColumnMap";
	public static final String START_LISTEN = "startListen";
	
	/**
	 * 返回的map是调用了set方法的字段，值是在调用startListen之前的值
	 * @return
	 */
	Map<String, Object> getChange_ColumnMap();
	
	/**
	 * 开始监听
	 */
	void startListen();
}
