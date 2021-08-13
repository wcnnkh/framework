package scw.orm.sql;

import scw.mapper.Field;

public interface Column extends ColumnDescriptor {
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();
}
