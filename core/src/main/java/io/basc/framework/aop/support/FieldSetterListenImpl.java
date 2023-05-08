package io.basc.framework.aop.support;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.mapper.Setter;

public class FieldSetterListenImpl implements FieldSetterListen, Serializable {
	public static final String FIELD_SETTER_MAP_FIELD_NAME = "field_setter_map";

	private Map<String, Object> field_setter_map;

	@Override
	public Map<String, Object> _getFieldSetterMap() {
		return field_setter_map;
	}

	@Override
	public void _fieldSet(MethodInvoker invoker, Setter setter, Object oldValue) {
		if (field_setter_map == null) {
			field_setter_map = new LinkedHashMap<String, Object>(8);
		}

		if (field_setter_map.containsKey(setter.getName())) {
			return;
		}

		field_setter_map.put(setter.getName(), oldValue);
	}

	@Override
	public void _clearFieldSetterMap() {
		this.field_setter_map = null;
	}

	public Object writeReplace() throws ObjectStreamException {
		return this;
	}
}
