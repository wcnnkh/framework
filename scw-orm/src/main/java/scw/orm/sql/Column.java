package scw.orm.sql;

import scw.mapper.Field;
import scw.orm.Property;

public interface Column extends ColumnDescriptor, Property{
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();
}
