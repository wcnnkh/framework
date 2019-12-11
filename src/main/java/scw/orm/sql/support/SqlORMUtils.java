package scw.orm.sql.support;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.lang.NotSupportException;
import scw.orm.ColumnFactory;
import scw.orm.Filter;
import scw.orm.ORMUtils;
import scw.orm.sql.DefaultSqlMapper;
import scw.orm.sql.DefaultTableNameMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableInstanceFactory;
import scw.orm.sql.TableNameMapping;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.enums.OperationType;
import scw.sql.Sql;

@SuppressWarnings("unchecked")
public final class SqlORMUtils {
	private static final SqlMapper SQL_MAPPER;

	private SqlORMUtils() {
	};

	static {
		String sqlMappingOperationsName = SystemPropertyUtils.getProperty("orm.sql.mapper");
		if (StringUtils.isEmpty(sqlMappingOperationsName)) {
			Collection<Filter> filters = new LinkedList<Filter>();
			filters.addAll(InstanceUtils.autoNewInstancesBySystemProperty(Filter.class, "orm.sql.filters",
					Collections.EMPTY_LIST));
			filters.add(new DefaultSqlFilter());
			NoArgsInstanceFactory noArgsInstanceFactory = InstanceUtils.autoNewInstanceBySystemProperty(
					NoArgsInstanceFactory.class, "orm.sql.table.instance.factory", new TableInstanceFactory());
			TableNameMapping tableNameMapping = InstanceUtils.autoNewInstanceBySystemProperty(TableNameMapping.class,
					"orm.sql.table.name.mapping", new DefaultTableNameMapping());
			SQL_MAPPER = new DefaultSqlMapper(tableNameMapping,
					InstanceUtils.autoNewInstanceBySystemProperty(ColumnFactory.class, "orm.sql.column.factory",
							ORMUtils.getColumnFactory()),
					filters, noArgsInstanceFactory);
		} else {
			try {
				SQL_MAPPER = InstanceUtils.autoNewInstance(sqlMappingOperationsName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static final SqlMapper getSqlMapper() {
		return SQL_MAPPER;
	}

	public static boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type);
	}

	public static Sql toSql(OperationType operationType, SqlDialect sqlDialect, Class<?> clazz, Object bean,
			String tableName) {
		switch (operationType) {
		case SAVE:
			return sqlDialect.toInsertSql(bean, clazz, tableName);
		case DELETE:
			return sqlDialect.toDeleteSql(bean, clazz, tableName);
		case SAVE_OR_UPDATE:
			return sqlDialect.toSaveOrUpdateSql(bean, clazz, tableName);
		case UPDATE:
			return sqlDialect.toUpdateSql(bean, clazz, tableName);
		default:
			throw new NotSupportException(operationType.name());
		}
	}
}
