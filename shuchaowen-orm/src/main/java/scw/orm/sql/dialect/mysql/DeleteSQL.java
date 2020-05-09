package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.sql.orm.enums.CasType;

public class DeleteSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public <T> DeleteSQL(SqlMapper mappingOperations, Class<? extends T> clazz, T obj, String tableName) {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>(tableFieldContext.getPrimaryKeys().size());
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);
		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sql, context.getColumn().getName());
			sql.append("=?");
			params.add(mappingOperations.getter(context, obj));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (mappingOperations.getCasType(context) == CasType.NOTHING) {
				continue;
			}

			sql.append(AND);
			keywordProcessing(sql, context.getColumn().getName());
			sql.append("=?");
			params.add(mappingOperations.getter(context, obj));
		}
		this.sql = sql.toString();
		this.params = params.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
