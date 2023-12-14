package io.basc.framework.orm.support;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.EntityMapper;

public final class OrmUtils {
	private static final EntityMapper MAPPER = SPI.global()
			.getServiceLoader(EntityMapper.class, DefaultEntityMapper.class).getServices().first();

	private OrmUtils() {
		throw new UnsupportedException(getClass().getName());
	}

	public static EntityMapper getMapper() {
		return MAPPER;
	}
}
