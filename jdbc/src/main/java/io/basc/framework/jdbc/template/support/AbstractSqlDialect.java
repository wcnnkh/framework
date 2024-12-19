package io.basc.framework.jdbc.template.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.SortOrder;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.jdbc.EasySql;
import io.basc.framework.jdbc.ResultSetMapper;
import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.SqlUtils;
import io.basc.framework.jdbc.template.SqlDialect;
import io.basc.framework.jdbc.template.SqlDialectException;
import io.basc.framework.jdbc.template.SqlType;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

/**
 * 标准的sql方言
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractSqlDialect extends ResultSetMapper implements SqlDialect {
	private String escapeCharacter = "`";

	public void concat(StringBuilder sb, String... strs) {
		if (strs == null || strs.length == 0) {
			return;
		}

		sb.append("concat(");
		for (int i = 0; i < strs.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(strs[i]);
		}
		sb.append(")");
	}

	public <T extends ColumnDescriptor> Map<IndexInfo, List<T>> getIndexGroups(Elements<? extends T> elements) {
		Map<IndexInfo, List<T>> groups = new LinkedHashMap<>();
		elements.forEach((column) -> {
			Elements<IndexInfo> indexs = column.getIndexs();
			if (!CollectionUtils.isEmpty(indexs)) {
				for (IndexInfo indexInfo : indexs) {
					List<T> columns = groups.get(indexInfo);
					if (columns == null) {
						columns = new ArrayList<>(8);
					}
					columns.add(column);
					groups.put(indexInfo, columns);
				}
			}
		});
		return groups;
	}

	public String getCreateTablePrefix() {
		return "CREATE TABLE IF NOT EXISTS";
	}

	// --------------以下为标准实现-----------------

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	@Override
	public Sql toCountSql(Sql sql) throws SqlDialectException {
		String str = sql.getSql();
		str = str.toLowerCase();
		EasySql countSql = new EasySql();
		countSql.append("select count(*) from (");
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex != -1 && str.indexOf(")", orderIndex) == -1) {
			countSql.append(SqlUtils.sub(sql, 0, orderIndex));
		} else {
			// 不存在 order by 子语句
			countSql.append(sql);
		}
		countSql.append(") as count_" + XUtils.getUUID());
		return countSql;
	}

	public Object toDataBaseValue(Value value) {
		if (value == null || !value.isPresent()) {
			return null;
		}

		SqlType sqlType = getSqlType(value.getTypeDescriptor().getType());
		if (sqlType == null) {
			return value;
		}

		return value.convert(TypeDescriptor.valueOf(sqlType.getType()), this);
	}

	/**
	 * 只是因为大部分数据库都支持limit请求，所以才写了此默认实现。 并非所以的数据库都支持limit语法，如: sql server
	 */
	@Override
	public Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException {
		Pair<Integer, Integer> range = StringUtils.indexOf(sql.getSql(), "(", ")");
		int fromIndex = 0;
		if (range != null) {
			fromIndex = range.getValue();
		}

		StringBuilder sb;
		if (sql.getSql().toLowerCase().indexOf(" limit ", fromIndex) != -1) {
			// 如果已经存在limit了，那么嵌套一上
			sb = new StringBuilder();
			sb.append("select * from (");
			sb.append(sql.getSql());
			sb.append(")");
		} else {
			sb = new StringBuilder(sql.getSql());
		}

		sb.append(" limit ").append(start);
		if (limit != 0) {
			sb.append(",").append(limit);
		}
		return new SimpleSql(sb.toString(), sql.getParams());
	}

	private void appendWhere(StringBuilder sb, List<Object> params, Elements<? extends Condition> conditions) {
		if (conditions == null || conditions.isEmpty()) {
			return;
		}

		sb.append(" where ");
		boolean first = true;
		Iterator<? extends Condition> conditionIterator = conditions.iterator();
		while (conditionIterator.hasNext()) {
			Condition condition = conditionIterator.next();

			if (!first) {
				if (RelationshipSymbol.AND.getName().equals(condition.getRelationshipSymbol().getName())) {
					sb.append(" and ");
				} else if (RelationshipSymbol.OR.getName().equals(condition.getRelationshipSymbol().getName())) {
					sb.append(" or ");
				} else {
					sb.append(condition.getRelationshipSymbol().getName());
				}
			}
			appendCondition(sb, params, condition);
			first = false;
		}
	}

	@SuppressWarnings("unchecked")
	protected void appendCondition(StringBuilder sb, List<Object> params, Condition condition) {
		if (ConditionSymbol.EQU.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append("=?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.ENDS_WITH.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" like ");
			concat(sb, "'%'", "?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.GEQ.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" >= ?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.LEQ.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" <= ?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.GTR.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" > ?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.IN.getName().equals(condition.getConditionSymbol().getName())) {
			List<Object> list;
			TypeDescriptor typeDescriptor = condition.getTypeDescriptor();
			if (typeDescriptor.isArray() || typeDescriptor.isCollection()) {
				list = (List<Object>) condition.convert(
						TypeDescriptor.collection(List.class, typeDescriptor.getElementTypeDescriptor()), this);
				typeDescriptor = typeDescriptor.getElementTypeDescriptor();
			} else {
				list = Arrays.asList(condition.getValue());
			}

			keywordProcessing(sb, condition.getName());
			Iterator<Object> iterator = list.iterator();
			sb.append(" in(");
			while (iterator.hasNext()) {
				sb.append("?");
				params.add(toDataBaseValue(Value.of(iterator.next(), typeDescriptor)));
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		} else if (ConditionSymbol.LSS.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" < ?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.CONTAINS.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" like ");
			concat(sb, "'%'", "?", "'%'");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.NEQ.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" is not ?");
			params.add(toDataBaseValue(condition));
		} else if (ConditionSymbol.STARTS_WITH.getName().equals(condition.getConditionSymbol().getName())) {
			keywordProcessing(sb, condition.getName());
			sb.append(" like ");
			concat(sb, "?", "'%'");
			params.add(toDataBaseValue(condition));
		} else {
			keywordProcessing(sb, condition.getName());
			sb.append(" ").append(condition.getConditionSymbol().getName()).append(" ?");
			params.add(toDataBaseValue(condition));
		}
	}

	protected void appendOrderBy(StringBuilder sb, List<Object> params, Elements<? extends Sort> sorts) {
		if (sorts == null || sorts.isEmpty()) {
			return;
		}

		sb.append(" order by ");
		Iterator<? extends Sort> iterator = sorts.iterator();
		while (iterator.hasNext()) {
			Sort sort = iterator.next();
			keywordProcessing(sb, sort.getExpression().getName());
			if (sort.getOrder() != null) {
				sb.append(" ");
				if (SortOrder.ASC.getName().equals(sort.getOrder().getName())) {
					sb.append("asc");
				} else if (SortOrder.DESC.getName().equals(sort.getOrder().getName())) {
					sb.append("desc");
				} else {
					sb.append(sort.getOrder().getName());
				}
			}
		}
	}

	@Override
	public Sql toSql(DeleteOperation operation) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sb.append("delete from ");
		keywordProcessing(sb, operation.getRepository().getName());
		appendWhere(sb, params, operation.getConditions());
		return new SimpleSql(sb.toString(), params.toArray());
	}

	protected String getInsertPrefix(InsertOperationSymbol operationSymbol) {
		return "insert into";
	}

	@Override
	public Sql toSql(InsertOperation operation) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();

		InsertOperationSymbol insertOperationSymbol = operation.getOperationSymbol();
		sb.append(getInsertPrefix(insertOperationSymbol));
		sb.append(" (");
		sb.append(operation.getColumns().map((e) -> e.getName()).collect(Collectors.joining(",")));
		sb.append(")");
		sb.append(" values ");
		sb.append(operation.getColumns().map((e) -> "?").collect(Collectors.joining(",")));
		params.addAll(operation.getColumns().map((e) -> toDataBaseValue(e)).toList());
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toSql(QueryOperation operation) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sb.append("select ");
		if (CollectionUtils.isEmpty(operation.getColumns())) {
			sb.append("*");
		} else {
			Iterator<? extends Expression> iterator = operation.getColumns().iterator();
			while (iterator.hasNext()) {
				Expression expression = iterator.next();
				keywordProcessing(sb, expression.getName());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
		}
		sb.append(" from ");
		keywordProcessing(sb, operation.getRepository().getName());

		appendWhere(sb, params, operation.getConditions());

		appendOrderBy(sb, params, operation.getOrders());

		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toSql(UpdateOperation operation) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sb.append("update ");
		keywordProcessing(sb, operation.getRepository().getName());
		sb.append(" set ");
		Iterator<? extends Expression> iterator = operation.getColumns().iterator();
		while (iterator.hasNext()) {
			Expression column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(toDataBaseValue(column));
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		appendWhere(sb, params, operation.getConditions());
		return new SimpleSql(sb.toString(), params.toArray());
	}
}
