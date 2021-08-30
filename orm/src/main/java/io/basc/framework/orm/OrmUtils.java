package io.basc.framework.orm;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalMapping;

public final class OrmUtils {
	private OrmUtils() {
		throw new NotSupportedException(getClass().getName());
	}

	private static final ObjectRelationalMapping ORM = Sys.env.getServiceLoader(ObjectRelationalMapping.class)
			.first(Sys.env.getInstanceSupplier(AnnotationObjectRelationalMapping.class));

	public static ObjectRelationalMapping getMapping() {
		return ORM;
	}
}
