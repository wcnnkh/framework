package scw.aop.support;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.aop.MethodInvoker;
import scw.mapper.Field;

public class FieldSetterListenImpl implements FieldSetterListen, Serializable {
	public static final String FIELD_SETTER_MAP_FIELD_NAME = "field_setter_map";
	
	private Map<String, Object> field_setter_map;

	public Map<String, Object> get_field_setter_map() {
		return field_setter_map;
	}

	public void field_setter(MethodInvoker invoker, Field field, Object oldValue) {
		if (field_setter_map == null) {
			field_setter_map = new LinkedHashMap<String, Object>(8);
		}

		if (field_setter_map.containsKey(field.getGetter().getName())) {
			return;
		}

		field_setter_map.put(field.getGetter().getName(), oldValue);
	}

	public void clear_field_setter_listen() {
		this.field_setter_map = null;
	}

	public Object writeReplace() throws ObjectStreamException {
		return this;
	}
}
