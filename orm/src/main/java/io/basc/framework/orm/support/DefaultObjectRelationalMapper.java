package io.basc.framework.orm.support;

import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalResolverExtend;

public class DefaultObjectRelationalMapper extends DefaultObjectRelationalResolver implements ObjectRelationalMapper {

	public DefaultObjectRelationalMapper() {
		addService(new AnnotationObjectRelationalResolverExtend());
	}
}
