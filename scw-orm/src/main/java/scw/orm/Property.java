package scw.orm;

import scw.mapper.Field;

public interface Property extends PropertyDescriptor{
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();
}
