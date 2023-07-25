package io.basc.framework.jdbc;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class SqlSplitSegment implements Sql {
	private final CharSequence sql;
	private final Object[] params;
	private final boolean storedProcedure;
	private final CharSequence separator;

	public SqlSplitSegment(Sql sql) {
		this(sql, null);
	}

	public SqlSplitSegment(CharSequence sql, Object[] params, boolean storedProcedure) {
		this(sql, params, storedProcedure, null);
	}

	public SqlSplitSegment(Sql sql, @Nullable CharSequence separator) {
		this(sql.getSql(), sql.getParams(), sql.isStoredProcedure(), separator);
	}

	public SqlSplitSegment(CharSequence sql, Object[] params, boolean storedProcedure,
			@Nullable CharSequence separator) {
		this.sql = sql;
		this.params = params;
		this.storedProcedure = storedProcedure;
		this.separator = separator;
	}

	public boolean isLast() {
		return separator == null;
	}

	public SqlSplitSegment trim() {
		return map((s) -> s.trim());
	}

	public SqlSplitSegment map(Function<Sql, Sql> map) {
		Sql sql = map.apply(this);
		return new SqlSplitSegment(sql, separator);
	}

	@Override
	public String getSql() {
		return sql.toString();
	}

	@Override
	public Object[] getParams() {
		return params;
	}

	@Override
	public boolean isStoredProcedure() {
		return storedProcedure;
	}

	@Nullable
	public CharSequence getSeparator() {
		return separator;
	}

	@Override
	public String toString() {
		return SqlUtils.toString(this);
	}

}
