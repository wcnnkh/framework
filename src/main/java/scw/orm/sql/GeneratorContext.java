package scw.orm.sql;

import scw.core.attribute.SimpleAttributes;
import scw.orm.MappingContext;
import scw.orm.sql.enums.OperationType;

@SuppressWarnings("serial")
public final class GeneratorContext extends SimpleAttributes<Object, Object> {
	private MappingContext mappingContext;
	private final ORMOperations ormOperations;
	private final OperationType operationType;
	private final Object bean;
	private final String tableName;
	private final SqlMapper sqlMapper;

	protected GeneratorContext(ORMOperations ormOperations, OperationType operationType, Object bean, String tableName,
			SqlMapper sqlMapper) {
		this.ormOperations = ormOperations;
		this.operationType = operationType;
		this.bean = bean;
		this.tableName = tableName;
		this.sqlMapper = sqlMapper;
	}

	public final MappingContext getMappingContext() {
		return mappingContext;
	}

	public final ORMOperations getOrmOperations() {
		return ormOperations;
	}

	public final OperationType getOperationType() {
		return operationType;
	}

	public final Object getBean() {
		return bean;
	}

	public final String getTableName() {
		return tableName;
	}

	protected final void setMappingContext(MappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	public final SqlMapper getSqlMapper() {
		return sqlMapper;
	}
}
