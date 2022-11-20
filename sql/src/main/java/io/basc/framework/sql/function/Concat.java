package io.basc.framework.sql.function;

import java.util.Arrays;
import java.util.Iterator;

import io.basc.framework.sql.EasySql;
import io.basc.framework.util.Pair;

public interface Concat {
	void concat(EasySql sql, Iterator<? extends Pair<? extends String, ? extends Object>> params);

	default void concat(EasySql sql, Iterable<? extends Pair<? extends String, ? extends Object>> params) {
		if (params == null) {
			return;
		}

		concat(sql, params.iterator());
	}

	@SuppressWarnings("unchecked")
	default void concat(EasySql sql, Pair<? extends String, ? extends Object>... params) {
		if (params == null) {
			return;
		}
		concat(sql, Arrays.asList(params));
	}
}
