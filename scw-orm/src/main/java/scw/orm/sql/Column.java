package scw.orm.sql;

import java.util.Collection;

import scw.mapper.Field;

/**
 * 字段
 * @author shuchaowen
 *
 */
public interface Column {
	String getGetterName();
	
	Collection<String> getSetterNames();

	Field getField();

	String getDescription();
}
