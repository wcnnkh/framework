package scw.aop.support;

import java.util.Map;

import scw.aop.WriteReplaceInterface;
import scw.core.reflect.MethodInvoker;
import scw.mapper.Field;

public interface FieldSetterListen extends WriteReplaceInterface{
	public static final String GET_CHANGE_MAP = "_getFieldSetterMap";
	public static final String CLEAR_FIELD_LISTEN = "_clearFieldSetterMap";

	/**
	 * 返回的map是调用了set方法的字段，值是在调用startFieldListen之前的值
	 * 
	 * @return
	 */
	Map<String, Object> _getFieldSetterMap();

	void _fieldSet(MethodInvoker invoker, Field field, Object oldValue);

	/**
	 * 清空监听数据
	 */
	void _clearFieldSetterMap();
}
