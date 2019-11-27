package scw.orm;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import scw.core.instance.InstanceUtils;
import scw.orm.cast.DefaultValueCastFilterChain;
import scw.orm.cast.ValueCastFilter;
import scw.orm.cast.ValueCastFilterChain;

public final class ORMUitls {
	private ORMUitls() {
	};

	@SuppressWarnings("unchecked")
	private static final Collection<ValueCastFilter> CAST_FILTERS = InstanceUtils
			.autoNewInstanceBySystemProperty(ValueCastFilter.class,
					"orm.cast.flters", Collections.EMPTY_LIST);

	public static Object cast(Type type, Object value) {
		ValueCastFilterChain chain = new DefaultValueCastFilterChain(
				CAST_FILTERS, null);
		return chain.doFilter(type, value);
	}
}
