package io.basc.framework.orm.support;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.EntityMapper;

public final class OrmUtils {
	private static final EntityMapper MAPPER = Sys.getEnv()
			.getServiceLoader(EntityMapper.class, DefaultEntityMapper.class).first();

	private OrmUtils() {
		throw new UnsupportedException(getClass().getName());
	}

	public static EntityMapper getMapper() {
		return MAPPER;
	}
}
