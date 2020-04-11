package scw.orm.sql.dialect.mysql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.annotation.Counter;
import scw.util.MultiIterator;

public class SaveOrUpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(SaveOrUpdateSQL.class);
	private static final String TEMP = ") ON DUPLICATE KEY UPDATE ";
	private static final String IF = "IF(";
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQL(SqlMapper mappingOperations, Class<?> clazz, Object obj, String tableName) {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new LinkedList<Object>();
		@SuppressWarnings("unchecked")
		Iterator<MappingContext> iterator = new MultiIterator<MappingContext>(
				tableFieldContext.getPrimaryKeys().iterator(), tableFieldContext.getNotPrimaryKeys().iterator());
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(cols, context.getColumn().getName());
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

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			Object v = mappingOperations.getter(context, obj);
			scw.orm.Column col = context.getColumn();
			Counter counter = col.getAnnotatedElement().getAnnotation(Counter.class);
			if (counter == null) {
				keywordProcessing(sb, col.getName());
				sb.append("=?");
				params.add(v);
			} else {
				if (v == null) {
					logger.warn("{}中计数器字段{}的值为空", clazz.getName(), col.getName());
					keywordProcessing(sb, col.getName());
					sb.append("=?");
					params.add(v);
				} else {
					keywordProcessing(sb, col.getName());
					sb.append("=");
					sb.append(IF);
					keywordProcessing(sb, col.getName());
					sb.append("+").append(v);
					sb.append(">=").append(counter.min());
					sb.append(AND);
					keywordProcessing(sb, col.getName());
					sb.append("+").append(v);
					sb.append("<=").append(counter.max());
					sb.append(",");
					keywordProcessing(sb, col.getName());
					sb.append("+?");
					params.add(v);
					sb.append(",");
					keywordProcessing(sb, col.getName());
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
