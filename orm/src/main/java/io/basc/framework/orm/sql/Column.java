package io.basc.framework.orm.sql;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.Property;

public interface Column extends ColumnDescriptor, Property{
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();
}
