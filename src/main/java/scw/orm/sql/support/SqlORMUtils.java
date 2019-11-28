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

import scw.core.exception.NotSupportException;
import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.FieldDefinitionFactory;
import scw.orm.Filter;
import scw.orm.sql.DefaultSqlMapper;
import scw.orm.sql.DefaultTableNameMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableFieldDefinitionFactory;
import scw.orm.sql.TableInstanceFactory;
import scw.orm.sql.TableNameMapping;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.dialect.DefaultSqlType;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.dialect.SqlType;
import scw.orm.sql.dialect.SqlTypeFactory;
import scw.orm.sql.enums.OperationType;
import scw.sql.Sql;

@SuppressWarnings("unchecked")
public final class SqlORMUtils {
	private static Logger logger = LoggerUtils.getLogger(SqlORMUtils.class);
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
			FieldDefinitionFactory fieldDefinitionFactory = InstanceUtils.autoNewInstanceBySystemProperty(
					FieldDefinitionFactory.class, "orm.sql.field.definition.factory",
					new TableFieldDefinitionFactory());
			NoArgsInstanceFactory noArgsInstanceFactory = InstanceUtils.autoNewInstanceBySystemProperty(
					NoArgsInstanceFactory.class, "orm.sql.table.instance.factory", new TableInstanceFactory());
			TableNameMapping tableNameMapping = InstanceUtils.autoNewInstanceBySystemProperty(TableNameMapping.class,
					"orm.sql.table.name.mapping", new DefaultTableNameMapping());
			SQL_MAPPER = new DefaultSqlMapper(tableNameMapping, fieldDefinitionFactory, filters,
					noArgsInstanceFactory);
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

	public static SqlType getSqlType(FieldDefinition fieldDefinition, SqlTypeFactory sqlTypeFactory) {
		String type = null;
		Column column = fieldDefinition.getAnnotation(Column.class);
		if (column != null) {
			type = column.type();
		}

		SqlType tempSqlType = StringUtils.isEmpty(type)
				? sqlTypeFactory.getSqlType(fieldDefinition.getField().getType()) : sqlTypeFactory.getSqlType(type);
		type = tempSqlType.getName();

		int len = -1;
		if (column != null) {
			len = column.length();
		}
		if (len <= 0) {
			len = tempSqlType.getLength();
		}
		return new DefaultSqlType(type, len);
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

	public static void registerCglibProxyTableBean(String pageName) {
		if (!StringUtils.isEmpty(pageName)) {
			logger.info("register proxy package:{}", pageName);
		}

		for (Class<?> type : ClassUtils.getClassList(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			FieldSetterListenUtils.createFieldSetterListenProxyClass(type);
		}
	}
}
