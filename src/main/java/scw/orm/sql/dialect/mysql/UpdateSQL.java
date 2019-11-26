package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.sql.orm.enums.CasType;

public class UpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(MappingOperations mappingOperations, Class<?> clazz, Object obj, String tableName)
			throws Exception {
		LinkedList<MappingContext> primaryKeys = SqlORMUtils.getPrimaryKeyFieldContexts(mappingOperations, clazz);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		LinkedList<MappingContext> notPrimaryKeys = SqlORMUtils.getNotPrimaryKeyFieldContexts(mappingOperations, clazz);
		List<Object> params = new ArrayList<Object>(notPrimaryKeys.size());
		Iterator<MappingContext> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sb, context.getFieldDefinition().getName());
			if (SqlORMUtils.getCasType(context.getFieldDefinition()) == CasType.AUTO_INCREMENT) {
				sb.append("=");
				keywordProcessing(sb, context.getFieldDefinition().getName());
				sb.append("+1");
			} else {
				sb.append("=?");
				params.add(mappingOperations.getter(context, obj));
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(WHERE);
		iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			params.add(mappingOperations.getter(context, obj));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (SqlORMUtils.getCasType(context.getFieldDefinition()) == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			params.add(mappingOperations.getter(context, obj));
		}
		this.sql = sb.toString();
		this.params = params.toArray(new Object[params.size()]);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
