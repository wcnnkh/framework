package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.dialect.SqlDialectException;

public final class SelectInIdSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static final String IN = " in (";
	private String sql;
	private Object[] params;

	public SelectInIdSQL(SqlMapper mappingOperations, Class<?> clazz, String tableName, Object[] primaryKeys,
			Collection<?> inPrimaryKeys) {
		if (CollectionUtils.isEmpty(inPrimaryKeys)) {
			throw new SqlDialectException("in 语句至少要有一个in条件");
		}

		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
		int whereSize = ArrayUtils.isEmpty(primaryKeys) ? 0 : primaryKeys.length;
		if (whereSize > tableFieldContext.getPrimaryKeys().size()) {
			throw new NullPointerException("primaryKeys length  greater than primary key lenght");
		}

		List<Object> params = new ArrayList<Object>(inPrimaryKeys.size() + whereSize);
		StringBuilder sb = new StringBuilder();
		if (whereSize > 0) {
			Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
			for (int i = 0; i < whereSize && iterator.hasNext(); i++) {
				if (sb.length() != 0) {
					sb.append(AND);
				}

				MappingContext context = iterator.next();
				keywordProcessing(sb, context.getColumn().getName());
				sb.append("=?");
				params.add(primaryKeys[i]);
			}
		}

		if (sb.length() != 0) {
			sb.append(AND);
		}

		keywordProcessing(sb, tableFieldContext.getPrimaryKeys().get(whereSize).getColumn().getName());
		sb.append(IN);
		Iterator<?> iterator = inPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			params.add(iterator.next());
			sb.append("?");
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		
		String where = sb.toString();
		sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(WHERE).append(where);
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
