package io.basc.framework.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.basc.framework.util.Pair;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;

public final class EasySql extends AbstractSql implements Serializable {
	private static final String[] KEYWORDS = new String[] { "where", "order by", "join", "having" };
	private static final String[] RELATIONSHIPS = new String[] { "and", "or", "not" };
	private static final long serialVersionUID = 1L;

	private Set<String> keywords;
	private ArrayList<Object> params;
	private Set<String> relationships;
	private StringBuilder sql;

	public EasySql() {
	}

	public EasySql(CharSequence sql) {
		append(sql);
	}

	public EasySql(CharSequence sql, Iterable<? extends Object> params) {
		append(sql, params);
	}

	public EasySql(CharSequence sql, Iterator<? extends Object> params) {
		append(sql, params);
	}

	public EasySql(CharSequence sql, Object... params) {
		append(sql, params);
	}

	public EasySql(Sql sql) {
		append(sql);
	}

	public EasySql addKeyword(String keyword) {
		String lowerKeywords = keyword.trim().toLowerCase();
		for (String key : KEYWORDS) {
			if (key.equals(lowerKeywords)) {
				return this;
			}
		}

		if (keywords == null) {
			keywords = new HashSet<>(8);
		}
		keywords.add(lowerKeywords);
		return this;
	}

	public EasySql addRelationship(String relationship) {
		String lowerRelationship = relationship.trim().toLowerCase();
		for (String key : RELATIONSHIPS) {
			if (key.equals(lowerRelationship)) {
				return this;
			}
		}

		if (keywords == null) {
			keywords = new HashSet<>(8);
		}
		relationships.add(lowerRelationship);
		return this;
	}

	public EasySql and() {
		return relationship("AND");
	}

	public EasySql append(CharSequence sql) {
		return append(sql, new Object[0]);
	}

	public EasySql appendName(String name) {
		if (StringUtils.isEmpty(name)) {

		}

		String[] array = StringUtils.splitToArray(name.trim(), ".");
		for (int i = 0; i < array.length - 1; i++) {
			append(array[i]).append(".");
		}
		String lastName = array[array.length - 1].trim();
		if (!lastName.startsWith("`")) {
			append("`");
		}

		append(lastName);

		if (!lastName.endsWith("`")) {
			append("`");
		}

		return this;
	}

	public EasySql append(CharSequence sql, Iterable<? extends Object> params) {
		return append(sql, params == null ? null : params.iterator());
	}

	public EasySql append(CharSequence sql, Iterator<? extends Object> params) {
		if (sql != null) {
			if (this.sql == null) {
				this.sql = new StringBuilder(sql);
			} else {
				this.sql.append(sql);
			}
		}

		if (params != null && params.hasNext()) {
			if (this.params == null) {
				this.params = new ArrayList<>();
			}
			params.forEachRemaining(this.params::add);
		}
		return this;
	}

	public EasySql append(CharSequence sql, Object... params) {
		return append(sql, params == null ? null : Arrays.asList(params));
	}

	public EasySql append(Sql sql) {
		if (sql == null) {
			return this;
		}

		if (sql instanceof EasySql) {
			EasySql easySql = (EasySql) sql;
			append(easySql.sql, easySql.params);
		} else {
			append(sql.getSql(), sql.getParams());
		}
		return this;
	}

	private String formatSql(String sql, Iterable<String> keys) {
		if (keys == null) {
			return null;
		}

		for (String key : keys) {
			if (StringUtils.endsWithIgnoreCase(sql, key)) {
				return sql.substring(0, sql.length() - key.length()).trim();
			}
		}
		return null;
	}

	@Override
	public Object[] getParams() {
		return params == null ? new Object[0] : params.toArray();
	}

	@Override
	public String getSql() {
		if (sql == null) {
			return "";
		}

		String sql = this.sql.toString().trim();
		while (true) {
			String formatSql = formatSql(sql, Arrays.asList(KEYWORDS));
			if (formatSql != null) {
				sql = formatSql;
				continue;
			}

			formatSql = formatSql(sql, Arrays.asList(RELATIONSHIPS));
			if (formatSql != null) {
				sql = formatSql;
				continue;
			}

			formatSql = formatSql(sql, keywords);
			if (formatSql != null) {
				sql = formatSql;
				continue;
			}

			formatSql = formatSql(sql, relationships);
			if (formatSql != null) {
				sql = formatSql;
				continue;
			}
			break;
		}
		return sql;
	}

	public EasySql in(String name, Iterable<? extends Object> values) {
		if (values == null) {
			return this;
		}

		return in(name, values.iterator());
	}

	public EasySql in(String name, Iterator<? extends Object> valueIterator) {
		return condition(name, "IN", valueIterator);
	}

	public EasySql in(String name, Object... values) {
		if (values == null || values.length == 0) {
			return this;
		}
		return in(name, Arrays.asList(values));
	}

	public int indexOfAnyKeyword(int formIndex) {
		String sql = this.sql == null ? "" : this.sql.toString().toLowerCase();
		return indexOfAnyKeyword(sql, formIndex, sql.length());
	}

	private int indexOfAnyKeyword(String sql, int fromIndex, int endIndex) {
		int index = -1;
		for (String keyword : KEYWORDS) {
			int i = indexOfKeyword(sql, keyword, fromIndex, endIndex);
			if (i == -1) {
				continue;
			}

			if (i > index) {
				index = i;
			}
		}

		if (keywords != null) {
			for (String keyword : keywords) {
				int i = indexOfKeyword(sql, keyword, fromIndex, endIndex);
				if (i == -1) {
					continue;
				}

				if (i > index) {
					index = i;
				}
			}
		}
		return index;
	}

	public int indexOfAnyRelationship(int formIndex) {
		String sql = this.sql == null ? "" : this.sql.toString().toLowerCase();
		return indexOfAnyRelationship(sql, formIndex, sql.length());
	}

	private int indexOfAnyRelationship(String sql, int formIndex, int endIndex) {
		int index = -1;
		for (String keyword : RELATIONSHIPS) {
			int i = indexOfRelationship(sql, keyword, formIndex, endIndex);
			if (i == -1) {
				continue;
			}

			if (i > index) {
				index = i;
			}
		}

		if (relationships != null) {
			for (String keyword : relationships) {
				int i = indexOfRelationship(sql, keyword, formIndex, endIndex);
				if (i == -1) {
					continue;
				}

				if (i > index) {
					index = i;
				}
			}
		}
		return index;
	}

	public int indexOfKeyword(String keyword, int formIndex) {
		String sql = this.sql == null ? "" : this.sql.toString().toLowerCase();
		return indexOfKeyword(sql, keyword, formIndex, sql.length());
	}

	private int indexOfKeyword(String sql, String keyword, int formIndex, int endIndex) {
		Pair<Integer, Integer> pair = StringUtils.indexOf(sql, "(", ")", formIndex, endIndex);
		if (pair == null) {
			// 不存在子语句
			int index = StringUtils.lastIndexOf(sql, "(", formIndex, endIndex);
			if (index != -1) {
				return StringUtils.indexOf(sql, keyword, index, endIndex);
			} else {
				return StringUtils.indexOf(sql, keyword, formIndex, endIndex);
			}
		} else {
			int index = indexOfKeyword(sql, keyword, formIndex, pair.getKey());
			if (index == -1) {
				index = indexOfKeyword(sql, keyword, pair.getValue(), endIndex);
			}
			return index;
		}
	}

	public int indexOfRelationship(String relationship, int formIndex) {
		String sql = this.sql == null ? "" : this.sql.toString().toLowerCase();
		return indexOfRelationship(sql, relationship, formIndex, sql.length());
	}

	private int indexOfRelationship(String sql, String relationship, int formIndex, int endIndex) {
		Pair<Integer, Integer> pair = StringUtils.indexOf(sql, "(", ")", formIndex, endIndex);
		if (pair == null) {
			// 不存在子语句
			int startIndex = StringUtils.lastIndexOf(sql, "(", formIndex, endIndex);
			int keywordIndex = startIndex == -1 ? indexOfAnyKeyword(sql, formIndex, endIndex)
					: indexOfAnyKeyword(sql, startIndex, endIndex);
			if (keywordIndex != -1) {
				return startIndex = keywordIndex;
			}

			return StringUtils.indexOf(sql, relationship, startIndex == -1 ? 0 : startIndex, endIndex);
		} else {
			int index = indexOfRelationship(sql, relationship, formIndex, pair.getKey());
			if (index == -1) {
				index = indexOfRelationship(sql, relationship, pair.getValue(), endIndex);
			}
			return index;
		}
	}

	public EasySql keyword(String keyword) {
		this.sql = new StringBuilder(getSql());
		addKeyword(keyword);
		if (indexOfAnyKeyword(0) != -1) {
			return this;
		}

		return append(" " + keyword.trim() + " ");
	}

	public EasySql not() {
		return relationship("NOT");
	}

	public EasySql or() {
		return relationship("OR");
	}

	public EasySql relationship(String relationship) {
		this.sql = new StringBuilder(getSql());
		addRelationship(relationship);
		if (indexOfAnyRelationship(0) == -1) {
			return this;
		}
		return append(" " + relationship.trim() + " ");
	}

	public EasySql where() {
		return keyword("WHERE");
	}

	public EasySql condition(String column, String condition, Object value) {
		return condition(column, condition, Arrays.asList(value));
	}

	public EasySql condition(String column, String condition, Object... values) {
		if (values == null) {
			return this;
		}

		return condition(column, condition, Arrays.asList(values));
	}

	public EasySql condition(String column, String condition, Iterable<? extends Object> values) {
		if (values == null) {
			return this;
		}

		return condition(column, condition, values.iterator());
	}

	public EasySql condition(String column, String condition, Iterator<? extends Object> valueIterator) {
		if (valueIterator == null || !valueIterator.hasNext()) {
			return this;
		}

		append(" `" + column + "` ");
		if (condition != null) {
			append(condition).append(" ");
		}

		boolean multiValue = false;
		while (valueIterator.hasNext()) {
			Object value = valueIterator.next();
			if (!multiValue && valueIterator.hasNext()) {
				// 多个
				append("(");
				multiValue = true;
			}

			append("?", value);
			if (valueIterator.hasNext()) {
				append(",");
			}
		}

		if (multiValue) {
			append(")");
		}
		return this;
	}

	public EasySql eq(String column, Object value) {
		return condition(column, "=", value);
	}

	public EasySql geq(String column, Object value) {
		return condition(column, ">=", value);
	}

	public EasySql gtr(String column, Object value) {
		return condition(column, ">", value);
	}

	public EasySql leq(String column, Object value) {
		return condition(column, "<=", value);
	}

	public EasySql lss(String column, Object value) {
		return condition(column, "<", value);
	}

	public EasySql neq(String column, Object value) {
		return condition(column, "!=", value);
	}

	public EasySql between(String column, Object start, Object end) {
		return append(" `" + column + "` BETWEEN ? AND ?", start, end);
	}

	public EasySql range(String column, Range<? extends Object> range) {
		if (range == null) {
			return this;
		}

		if (range.getLowerBound().isBounded() && range.getUpperBound().isBounded()
				&& range.getLowerBound().isInclusive() && !range.getUpperBound().isInclusive()) {
			return between(column, range.getLowerBound().get(), range.getUpperBound().get());
		}

		if (range.getLowerBound().isBounded()) {
			and().condition(column, range.getLowerBound().isInclusive() ? ">=" : ">", range.getLowerBound().get());
		}

		if (range.getUpperBound().isBounded()) {
			and().condition(column, range.getUpperBound().isInclusive() ? "<=" : "<", range.getUpperBound().get());
		}
		return this;
	}

	private boolean isFirstColumnByKeyword(String keyword, String separator) {
		int index = indexOfKeyword(keyword, 0);
		if (index == -1) {
			return true;
		}

		if (separator == null) {
			return true;
		}

		return this.sql == null ? true : (this.sql.indexOf(separator, index) == -1);
	}

	public EasySql order(String keyword, Iterable<String> names, String connector, String order, String separator) {
		if (names == null) {
			return this;
		}
		return order(keyword, names.iterator(), connector, order, separator);
	}

	public EasySql order(String keyword, Iterator<String> nameIterator, String connector, String order,
			String separator) {
		if (nameIterator == null || !nameIterator.hasNext()) {
			return this;
		}

		if (separator != null && isFirstColumnByKeyword(keyword, separator)) {
			append(separator);
		}

		while (nameIterator.hasNext()) {
			String name = nameIterator.next();
			appendName(" ").appendName(name);
			if (connector != null) {
				append(" " + connector);
			}

			if (order != null) {
				append(" " + order);
			}

			if (separator != null && nameIterator.hasNext()) {
				append(separator);
			}
		}
		return this;
	}

	public EasySql orderBy() {
		return keyword("ORDER BY");
	}

	public EasySql desc(String... columns) {
		orderBy();
		return order("ORDER BY", Arrays.asList(columns), null, "DESC", ",");
	}

	public EasySql asc(String... columns) {
		// 可以忽略ASC关键字
		orderBy();
		return order("ORDER BY", Arrays.asList(columns), null, null, ",");
	}

	public EasySql select(String... columns) {
		keyword("SELECT ");
		return order("SELECT", Arrays.asList(columns), null, null, ",");
	}

	public EasySql from(String... names) {
		keyword(" FROM ");
		return order("FROM", Arrays.asList(names), null, null, ",");
	}
}
