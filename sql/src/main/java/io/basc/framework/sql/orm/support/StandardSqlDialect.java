package io.basc.framework.sql.orm.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.DeleteOperationSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.SortSymbol;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.sql.EasySql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sql.orm.SqlDialectException;
import io.basc.framework.sql.orm.SqlType;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.Value;

/**
 * 标准的sql方言
 * 
 * @author wcnnkh
 *
 */
public abstract class StandardSqlDialect extends DefaultTableMapper implements SqlDialect {
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
				list = Arrays.asList(condition.getSource());
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
		} else if (ConditionSymbol.LIKE.getName().equals(condition.getConditionSymbol().getName())) {
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
			if (sort.getSymbol() != null) {
				sb.append(" ");
				if (SortSymbol.ASC.getName().equals(sort.getSymbol().getName())) {
					sb.append("asc");
				} else if (SortSymbol.DESC.getName().equals(sort.getSymbol().getName())) {
					sb.append("desc");
				} else {
					sb.append(sort.getSymbol().getName());
				}
			}
		}
	}

	@Override
	public Sql toSql(Operation operation) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();
		if (operation instanceof QueryOperation) {
			QueryOperation queryOperation = (QueryOperation) operation;
			sb.append("select ");
			if (CollectionUtils.isEmpty(queryOperation.getColumns())) {
				sb.append("*");
			} else {
				Iterator<? extends Expression> iterator = queryOperation.getColumns().iterator();
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

			appendWhere(sb, params, queryOperation.getConditions());

			appendOrderBy(sb, params, queryOperation.getSorts());
		} else if (operation instanceof UpdateOperation) {
			UpdateOperation updateOperation = (UpdateOperation) operation;
			sb.append("update ");
			keywordProcessing(sb, operation.getRepository().getName());
			sb.append(" set ");
			Iterator<? extends Expression> iterator = updateOperation.getColumns().iterator();
			while (iterator.hasNext()) {
				Expression column = iterator.next();
				keywordProcessing(sb, column.getName());
				sb.append("=?");
				params.add(toDataBaseValue(column));
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			appendWhere(sb, params, updateOperation.getConditions());
		} else if (operation.getOperationSymbol().getName().equals(DeleteOperationSymbol.DELETE.getName())) {
			DeleteOperation deleteOperation = (DeleteOperation) operation;
			sb.append("delete from ");
			keywordProcessing(sb, operation.getRepository().getName());
			appendWhere(sb, params, deleteOperation.getConditions());
		} else if (operation instanceof InsertOperation) {
			InsertOperation insertOperation = (InsertOperation) operation;
			sb.append("insert into ");
			sb.append("(");
			sb.append(insertOperation.getColumns().map((e) -> e.getName()).collect(Collectors.joining(",")));
			sb.append(")");
			sb.append(" values ");
			sb.append(insertOperation.getColumns().map((e) -> "?").collect(Collectors.joining(",")));
			params.addAll(insertOperation.getColumns().map((e) -> toDataBaseValue(e)).toList());
		}else {
			throw new UnsupportedOperationException(operation.toString());
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}
}
