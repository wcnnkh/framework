package io.basc.framework.sql;

import java.util.Arrays;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;

public abstract class AbstractSql implements Sql {

	@Override
	public int hashCode() {
		return CollectionUtils.hashCode(Arrays.asList(getSql(), getParams()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Sql) {
			return ObjectUtils.equals(getSql(), ((Sql) obj).getSql())
					&& ObjectUtils.equals(getParams(), ((Sql) obj).getParams());
		}
		return false;
	}

	@Override
	public String toString() {
		return SqlUtils.toString(getSql(), getParams());
	}
}
