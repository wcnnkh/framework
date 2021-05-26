package scw.sql.orm;

import scw.env.Sys;
import scw.lang.NotSupportedException;
import scw.sql.Sql;
import scw.sql.orm.dialect.SqlDialect;
import scw.sql.orm.enums.OperationType;

public final class OrmUtils {
	private static final ObjectRelationalMapping OBJECT_RELATIONAL_MAPPING = Sys.loadService(ObjectRelationalMapping.class, "scw.sql.orm.ObjectRelationalMapping");

	private OrmUtils() {
	};

	public static ObjectRelationalMapping getObjectRelationalMapping() {
		return OBJECT_RELATIONAL_MAPPING;
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
			throw new NotSupportedException(operationType.name());
		}
	}
}
