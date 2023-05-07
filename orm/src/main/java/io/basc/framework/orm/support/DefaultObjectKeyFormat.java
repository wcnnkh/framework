package io.basc.framework.orm.support;

import java.util.Iterator;

import io.basc.framework.env.Sys;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.Property;
import io.basc.framework.value.Value;

public class DefaultObjectKeyFormat implements ObjectKeyFormat {
	/**
	 * 默认对象主键的连接符
	 */
	private static final String OBJECT_KEY_CONNECTOR = Sys.getEnv().getProperties()
			.get("io.basc.framework.orm.key.connector").or(":").getAsString();

	private final String connector;

	public DefaultObjectKeyFormat() {
		this(OBJECT_KEY_CONNECTOR);
	}

	public DefaultObjectKeyFormat(String connector) {
		this.connector = connector;
	}

	@Override
	public String getObjectKeyByIds(EntityMapping<? extends Property> structure, Iterator<?> ids) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(structure.getName());
		Iterator<? extends Property> primaryKeys = structure.getPrimaryKeys().iterator();
		while (primaryKeys.hasNext() && ids.hasNext()) {
			appendObjectKeyByValue(sb, primaryKeys.next(), ids.next());
		}
		return sb.toString();
	}

	private void appendObjectKeyByValue(StringBuilder appendable, Property property, Object value) {
		appendable.append(connector);
		appendable.append(property.getName());
		appendable.append(connector);
		String str = String.valueOf(value);
		str = str.replaceAll(connector, "\\" + connector);
		appendable.append(str);
	}

	@Override
	public <T> String getObjectKey(EntityMapping<? extends Property> structure, Value bean) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(structure.getName());
		structure.getPrimaryKeys().forEach((column) -> {
			appendObjectKeyByValue(sb, column, column.getGetters().first().get(bean));
		});
		return sb.toString();
	}
}
