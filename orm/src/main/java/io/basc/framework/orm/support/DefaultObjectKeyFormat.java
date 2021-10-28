package io.basc.framework.orm.support;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.env.Sys;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.Property;

public class DefaultObjectKeyFormat implements ObjectKeyFormat {
	/**
	 * 默认对象主键的连接符
	 */
	private static final String OBJECT_KEY_CONNECTOR = Sys.env.getValue("io.basc.framework.orm.key.connector",
			String.class, ":");

	private final String connector;

	public DefaultObjectKeyFormat() {
		this(OBJECT_KEY_CONNECTOR);
	}

	public DefaultObjectKeyFormat(String connector) {
		this.connector = connector;
	}

	@Override
	public String getObjectKeyByIds(EntityStructure<?> structure, Collection<Object> ids) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(structure.getName());
		Iterator<? extends Property> primaryKeys = structure.getPrimaryKeys().iterator();
		Iterator<Object> valueIterator = ids.iterator();
		while (primaryKeys.hasNext() && valueIterator.hasNext()) {
			appendObjectKeyByValue(sb, primaryKeys.next(), valueIterator.next());
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
	public <T> String getObjectKey(EntityStructure<?> structure, T bean) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(structure.getName());
		for (Property column : structure.getPrimaryKeys()) {
			appendObjectKeyByValue(sb, column, column.getField().get(bean));
		}
		return sb.toString();
	}
}
