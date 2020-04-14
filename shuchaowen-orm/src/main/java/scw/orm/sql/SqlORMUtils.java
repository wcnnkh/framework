package scw.orm.sql;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.lang.UnsupportedException;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.enums.OperationType;
import scw.sql.Sql;

public final class SqlORMUtils {
	private static final TableNameMapping TABLE_NAME_MAPPING = InstanceUtils
			.getSystemConfiguration(TableNameMapping.class);
	private static final SqlColumnFactory SQL_COLUMN_FACTORY = InstanceUtils
			.getSystemConfiguration(SqlColumnFactory.class);
	private static final SqlMapper SQL_MAPPER = InstanceUtils
			.getSystemConfiguration(SqlMapper.class);

	private SqlORMUtils() {
	};

	public static TableNameMapping getTableNameMapping() {
		return TABLE_NAME_MAPPING;
	}

	public static SqlColumnFactory getSqlColumnFactory() {
		return SQL_COLUMN_FACTORY;
	}

	public static final SqlMapper getSqlMapper() {
		return SQL_MAPPER;
	}

	public static boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotatedElement().getAnnotation(Index.class) != null;
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type);
	}

	public static Sql toSql(OperationType operationType, SqlDialect sqlDialect,
			Class<?> clazz, Object bean, String tableName) {
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
			throw new UnsupportedException(operationType.name());
		}
	}
}
