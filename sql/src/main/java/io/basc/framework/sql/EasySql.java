package io.basc.framework.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;

public final class EasySql extends AbstractSql implements Serializable {
	private static final String[] KEYWORDS = new String[] { "where", "join", "having" };
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
			return null;
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

	private int indexOfAnyKeyword(String sql, int fromIndex, int endIndex) {
		int index = -1;
		for (String keyword : KEYWORDS) {
			int i = indexOfKeyword(sql, keyword, fromIndex, endIndex);
			if (i == -1) {
				continue;
			}

			if (i < index) {
				index = i;
			}
		}

		if (keywords != null) {
			for (String keyword : keywords) {
				int i = indexOfKeyword(sql, keyword, fromIndex, endIndex);
				if (i == -1) {
					continue;
				}

				if (i < index) {
					index = i;
				}
			}
		}
		return index;
	}

	public int indexOfAnyRelationship(String sql, int formIndex, int endIndex) {
		int index = -1;
		for (String keyword : RELATIONSHIPS) {
			int i = indexOfRelationship(sql, keyword, formIndex, endIndex);
			if (i == -1) {
				continue;
			}

			if (i < index) {
				index = i;
			}
		}

		if (relationships != null) {
			for (String keyword : relationships) {
				int i = indexOfRelationship(sql, keyword, formIndex, endIndex);
				if (i == -1) {
					continue;
				}

				if (i < index) {
					index = i;
				}
			}
		}
		return index;
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
		addKeyword(keyword);
		String sql = this.sql == null ? "" : this.sql.toString().toLowerCase();
		if (indexOfAnyKeyword(sql, 0, sql.length()) != -1) {
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
		addRelationship(relationship);
		String sql = this.sql == null ? "" : this.sql.toString();
		if (indexOfAnyRelationship(sql, 0, sql.length()) == -1) {
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

		if (sql == null) {
			sql = new StringBuilder();
		}

		if (params == null) {
			this.params = new ArrayList<>();
		}

		sql.append(" `").append(column).append("` ");
		if (condition != null) {
			sql.append(condition).append(" ");
		}

		boolean multiValue = false;
		while (valueIterator.hasNext()) {
			Object value = valueIterator.next();
			if (!multiValue && valueIterator.hasNext()) {
				// 多个
				sql.append("(");
				multiValue = true;
			}

			sql.append("?");
			params.add(value);
			if (valueIterator.hasNext()) {
				sql.append(",");
			}
		}

		if (multiValue) {
			sql.append(")");
		}
		return this;
	}

	/**
	 * =
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql eq(String column, Object value) {
		return condition(column, "=", value);
	}

	/**
	 * >=
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql geq(String column, Object value) {
		return condition(column, ">=", value);
	}

	/**
	 * >
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql gtr(String column, Object value) {
		return condition(column, ">", value);
	}

	/**
	 * <=
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql leq(String column, Object value) {
		return condition(column, "<=", value);
	}

	/**
	 * <
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql lss(String column, Object value) {
		return condition(column, "<", value);
	}

	/**
	 * !=
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public EasySql neq(String column, Object value) {
		return condition(column, "!=", value);
	}

	/**
	 * @param column
	 * @param start  包含
	 * @param end    不包含
	 * @return
	 */
	public EasySql between(String column, Object start, Object end) {
		return append(" `" + column + "` BETWEEN ? AND ?", start, end);
	}
}
