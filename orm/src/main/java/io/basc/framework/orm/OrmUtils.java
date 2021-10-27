package io.basc.framework.orm;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotSupportedException;

public final class OrmUtils {
	private OrmUtils() {
		throw new NotSupportedException(getClass().getName());
	}

	private static final ObjectRelationalMapping ORM = Sys.env.getServiceLoader(ObjectRelationalMapping.class)
			.first(Sys.env.getInstanceSupplier(DefaultObjectRelationalMapping.class));

	public static ObjectRelationalMapping getMapping() {
		return ORM;
	}
}
