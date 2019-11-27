package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;
import scw.sql.orm.enums.CasType;

public class UpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(MappingOperations mappingOperations, Class<?> clazz, Object obj, String tableName)
			throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		List<Object> params = new ArrayList<Object>(tableFieldContext.getNotPrimaryKeys().size());
		Iterator<MappingContext> iterator = tableFieldContext.getNotPrimaryKeys().iterator();
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
		iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			params.add(mappingOperations.getter(context, obj));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
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
