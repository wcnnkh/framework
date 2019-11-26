package scw.orm.sql.dialect.mysql;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.core.reflect.FieldDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.sql.orm.annotation.Counter;

public class SaveOrUpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(SaveOrUpdateSQL.class);
	private static final String TEMP = ") ON DUPLICATE KEY UPDATE ";
	private static final String IF = "IF(";
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQL(MappingOperations mappingOperations, Class<?> clazz, Object obj, String tableName)
			throws Exception {
		Collection<MappingContext> primaryKeys = SqlORMUtils.getPrimaryKeyFieldContexts(mappingOperations, clazz);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new LinkedList<Object>();
		Iterator<MappingContext> iterator = SqlORMUtils.getTableFieldContexts(mappingOperations, clazz).iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(cols, context.getFieldDefinition().getName());
			values.append("?");
			params.add(mappingOperations.getter(context, obj));

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append(INSERT_INTO_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append("(");
		sb.append(cols);
		sb.append(InsertSQL.VALUES);
		sb.append(values);
		sb.append(TEMP);

		iterator = SqlORMUtils.getNotPrimaryKeyFieldContexts(mappingOperations, clazz).iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			Object v = mappingOperations.getter(context, obj);
			FieldDefinition fieldDefinition = context.getFieldDefinition();
			Counter counter = fieldDefinition.getAnnotation(Counter.class);
			if (counter == null) {
				keywordProcessing(sb, fieldDefinition.getName());
				sb.append("=?");
				params.add(v);
			} else {
				if (v == null) {
					logger.warn("{}中计数器字段{}的值为空", clazz.getName(), fieldDefinition.getName());
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append("=?");
					params.add(v);
				} else {
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append("=");
					sb.append(IF);
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append("+").append(v);
					sb.append(">=").append(counter.min());
					sb.append(AND);
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append("+").append(v);
					sb.append("<=").append(counter.max());
					sb.append(",");
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append("+?");
					params.add(v);
					sb.append(",");
					keywordProcessing(sb, fieldDefinition.getName());
					sb.append(")");
				}
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		this.sql = sb.toString();
		this.params = params.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
