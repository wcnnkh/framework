package io.basc.framework.orm.support;

import java.util.Collections;
import java.util.List;

import io.basc.framework.data.domain.Tree;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Processor;

public final class OrmUtils {
	private static final ObjectRelationalMapper MAPPER = Sys.getEnv()
			.getServiceLoader(ObjectRelationalMapper.class, DefaultEntityMapper1.class).first();

	private OrmUtils() {
		throw new UnsupportedException(getClass().getName());
	}

	public static ObjectRelationalMapper getMapper() {
		return MAPPER;
	}

	public static <K, V, T, E extends Throwable> List<Tree<Pair<K, V>>> parseTrees(List<Property> properties,
			List<? extends T> entitys,
			Processor<? super Pair<Property, T>, ? extends Pair<K, V>, ? extends E> processor) throws E {
		if (CollectionUtils.isEmpty(properties) || CollectionUtils.isEmpty(entitys)) {
			return Collections.emptyList();
		}

		return Tree.parse(entitys, 0, properties.size(),
				(e) -> processor.process(new Pair<Property, T>(properties.get(e.getKey()), e.getValue())));
	}
}
