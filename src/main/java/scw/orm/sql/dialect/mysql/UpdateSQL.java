package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.enums.CasType;

public class UpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(SqlMapper mappingOperations, Class<?> clazz, Object obj, String tableName) throws Exception {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
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
			keywordProcessing(sb, context.getColumn().getName());
			if (mappingOperations.getCasType(context) == CasType.AUTO_INCREMENT) {
				sb.append("=");
				keywordProcessing(sb, context.getColumn().getName());
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
			keywordProcessing(sb, context.getColumn().getName());
			sb.append("=?");
			params.add(mappingOperations.getter(context, obj));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (mappingOperations.getCasType(context) == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, context.getColumn().getName());
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
