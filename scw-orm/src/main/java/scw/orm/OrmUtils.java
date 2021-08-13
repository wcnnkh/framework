package scw.orm;

import scw.env.Sys;
import scw.lang.NotSupportedException;
import scw.orm.annotation.AnnotationObjectRelationalMapping;

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
