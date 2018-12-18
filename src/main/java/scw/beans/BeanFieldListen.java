package scw.beans;

import java.io.Serializable;
import java.util.Map;

import scw.common.FieldInfo;

public interface BeanFieldListen extends Serializable{
	public static final String GET_CHANGE_MAP = "get_field_change_map";
	public static final String START_LISTEN = "start_field_listen";
	public static final String IS_START_LISTEN = "is_start_field_listen";
	
	/**
	 * 返回的map是调用了set方法的字段，值是在调用startFieldListen之前的值
	 * @return
	 */
	Map<String, Object> get_field_change_map();
	
	void field_change(FieldInfo fieldInfo, Object oldValue);
	
	/**
	 * 开始监听
	 */
	void start_field_listen();
}
