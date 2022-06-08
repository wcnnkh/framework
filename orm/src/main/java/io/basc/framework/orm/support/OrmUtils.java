package io.basc.framework.orm.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.basc.framework.data.domain.Tree;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.stream.Processor;

public final class OrmUtils {
	private static final DefaultObjectRelationalMapper MAPPER = new DefaultObjectRelationalMapper();

	static {
		MAPPER.configure(Sys.env);
	}

	private OrmUtils() {
		throw new NotSupportedException(getClass().getName());
	}

	public static ObjectRelationalMapper<Map<String, Object>, OrmException> getMapper() {
		return MAPPER;
	}

	public static <K, V, T, E extends Throwable> List<Tree<Pair<K, V>>> parseTrees(List<Property> properties,
			List<? extends T> entitys, Processor<Pair<Property, T>, Pair<K, V>, E> processor) throws E {
		if (CollectionUtils.isEmpty(properties) || CollectionUtils.isEmpty(entitys)) {
			return Collections.emptyList();
		}

		return Tree.parse(entitys, 0, properties.size(),
				(e) -> processor.process(new Pair<Property, T>(properties.get(e.getKey()), e.getValue())));
	}
}
