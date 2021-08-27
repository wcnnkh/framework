package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

public interface Property extends PropertyDescriptor{
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();
}
