package io.basc.framework.aop.support;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.mapper.Field;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldSetterListenImpl implements FieldSetterListen, Serializable {
	public static final String FIELD_SETTER_MAP_FIELD_NAME = "field_setter_map";

	private Map<String, Object> field_setter_map;

	@Override
	public Map<String, Object> _getFieldSetterMap() {
		return field_setter_map;
	}

	@Override
	public void _fieldSet(MethodInvoker invoker, Field field, Object oldValue) {
		if (field_setter_map == null) {
			field_setter_map = new LinkedHashMap<String, Object>(8);
		}

		if (field_setter_map.containsKey(field.getGetter().getName())) {
			return;
		}

		field_setter_map.put(field.getGetter().getName(), oldValue);
	}

	@Override
	public void _clearFieldSetterMap() {
		this.field_setter_map = null;
	}

	public Object writeReplace() throws ObjectStreamException {
		return this;
	}
}
