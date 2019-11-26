package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.core.exception.ParameterException;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.sql.orm.enums.CasType;

public class DeleteSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public DeleteSQL(MappingOperations mappingOperations, Class<?> clazz, String tableName, Object[] parimayKeys)
			throws Exception {
		Collection<MappingContext> fieldDefinitions = SqlORMUtils.getPrimaryKeyFieldContexts(mappingOperations, clazz);
		if (fieldDefinitions.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (fieldDefinitions.size() != parimayKeys.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		this.params = parimayKeys;

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		Iterator<MappingContext> iterator = SqlORMUtils.getPrimaryKeyFieldContexts(mappingOperations, clazz).iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sql, context.getFieldDefinition().getName());
			sql.append("=?");
		}
		this.sql = sql.toString();
	}

	public <T> DeleteSQL(MappingOperations mappingOperations, Class<? extends T> clazz, T obj, String tableName)
			throws Exception {
		Collection<MappingContext> fieldDefinitions = SqlORMUtils.getPrimaryKeyFieldContexts(mappingOperations, clazz);
		if (fieldDefinitions.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>(fieldDefinitions.size());
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);
		Iterator<MappingContext> iterator = fieldDefinitions.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sql, context.getFieldDefinition().getName());
			sql.append("=?");
			params.add(mappingOperations.getter(context, obj));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		iterator = SqlORMUtils.getNotPrimaryKeyFieldContexts(mappingOperations, clazz).iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (SqlORMUtils.getCasType(context.getFieldDefinition()) == CasType.NOTHING) {
				continue;
			}

			sql.append(AND);
			keywordProcessing(sql, context.getFieldDefinition().getName());
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
