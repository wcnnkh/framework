package scw.mapper;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;

public class FieldFilters implements FieldFilter {
	private Iterable<FieldFilter> iterable;

	public FieldFilters(FieldFilter... filters) {
		this(ArrayUtils.isEmpty(filters) ? null : Arrays.asList(filters));
	}

	public FieldFilters(Iterable<FieldFilter> iterable) {
		this.iterable = iterable;
	}

	public boolean accept(Field field) {
		if (iterable == null) {
			return true;
		}

		for (FieldFilter filter : iterable) {
			if (!filter.accept(field)) {
				return false;
			}
		}
		return true;
	}
}
