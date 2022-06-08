package io.basc.framework.orm.support;

import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.MapAccess;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.orm.OrmException;

public class DefaultObjectRelationalMapper extends AbstractObjectRelationalMapper<Map<String, Object>, OrmException> {

	@Override
	public ObjectAccess<OrmException> getObjectAccess(Map<String, Object> source, TypeDescriptor sourceType) {
		return new MapAccess<OrmException>(source);
	}

}
