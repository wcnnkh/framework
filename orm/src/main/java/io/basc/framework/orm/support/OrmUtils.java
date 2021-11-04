package io.basc.framework.orm.support;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.EntityMetadata;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.Property;

public final class OrmUtils {
	private OrmUtils() {
		throw new NotSupportedException(getClass().getName());
	}

	private static final ObjectRelationalMapping ORM = Sys.env
			.getServiceLoader(ObjectRelationalMapping.class, DefaultObjectRelationalMapping.class).first();

	public static ObjectRelationalMapping getMapping() {
		return ORM;
	}

	public static StandardEntityStructure<Property> init(ObjectRelationalMapping objectRelationalMapping,
			Class<?> entityClass) {
		EntityMetadata entityMetadata = objectRelationalMapping.resolveMetadata(entityClass);
		return new StandardEntityStructure<>(entityMetadata);
	}
}
