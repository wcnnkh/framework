package io.basc.framework.orm.support;

import io.basc.framework.data.domain.Tree;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.EntityMetadata;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.stream.Processor;

import java.util.Collections;
import java.util.List;

public final class OrmUtils {
	private OrmUtils() {
		throw new NotSupportedException(getClass().getName());
	}

	private static final ObjectRelationalMapper ORM = Sys.env
			.getServiceLoader(ObjectRelationalMapper.class,
					DefaultObjectRelationalMapper.class).first();

	public static ObjectRelationalMapper getMapping() {
		return ORM;
	}

	public static StandardEntityStructure<Property> init(
			ObjectRelationalMapper objectRelationalMapping,
			Class<?> entityClass) {
		EntityMetadata entityMetadata = objectRelationalMapping
				.resolveMetadata(entityClass);
		return new StandardEntityStructure<>(entityMetadata);
	}

	public static <K, V, T, E extends Throwable> List<Tree<Pair<K, V>>> parseTrees(
			List<Property> properties, List<? extends T> entitys,
			Processor<Pair<Property, T>, Pair<K, V>, E> processor) throws E {
		if (CollectionUtils.isEmpty(properties)
				|| CollectionUtils.isEmpty(entitys)) {
			return Collections.emptyList();
		}

		return Tree.parse(entitys, 0, properties.size(), (e) -> processor
				.process(new Pair<Property, T>(properties.get(e.getKey()), e
						.getValue())));
	}
}
