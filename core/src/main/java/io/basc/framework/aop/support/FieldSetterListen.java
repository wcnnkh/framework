package io.basc.framework.aop.support;

import java.util.Map;

import io.basc.framework.aop.WriteReplaceInterface;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.mapper.Setter;

public interface FieldSetterListen extends WriteReplaceInterface {
	public static final String GET_CHANGE_MAP = "_getFieldSetterMap";
	public static final String CLEAR_FIELD_LISTEN = "_clearFieldSetterMap";

	/**
	 * 返回的map是调用了set方法的字段，值是在调用startFieldListen之前的值
	 * 
	 * @return {@link Map}
	 */
	Map<String, Object> _getFieldSetterMap();

	void _fieldSet(MethodInvoker invoker, Setter setter, Object oldValue);

	/**
	 * 清空监听数据
	 */
	void _clearFieldSetterMap();
}
