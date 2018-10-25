package shuchaowen.core.beans;

import java.io.Serializable;
import java.util.Map;

public interface BeanListen extends Serializable{
	public static final String GET_CHANGE_MAP = "get_field_change_map";
	public static final String START_LISTEN = "start_field_listen";
	
	/**
	 * 返回的map是调用了set方法的字段，值是在调用startFieldListen之前的值
	 * @return
	 */
	Map<String, Object> get_field_change_map();
	
	void field_change(String field, Object oldValue);
	
	/**
	 * 开始监听
	 */
	void start_field_listen();
}
