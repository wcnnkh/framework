package scw.aop.support;

import java.util.Map;

import scw.aop.ProxyContext;
import scw.aop.WriteReplaceInterface;
import scw.mapper.Field;

public interface FieldSetterListen extends WriteReplaceInterface{
	public static final String GET_CHANGE_MAP = "get_field_setter_map";
	public static final String CLEAR_FIELD_LISTEN = "clear_field_setter_listen";

	/**
	 * 返回的map是调用了set方法的字段，值是在调用startFieldListen之前的值
	 * 
	 * @return
	 */
	Map<String, Object> get_field_setter_map();

	void field_setter(ProxyContext context, Field field, Object oldValue);

	/**
	 * 清空监听数据
	 */
	void clear_field_setter_listen();
}
