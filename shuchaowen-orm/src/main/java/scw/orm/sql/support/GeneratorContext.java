package scw.orm.sql.support;

import scw.orm.MappingContext;
import scw.orm.sql.ORMOperations;
import scw.orm.sql.SqlMapper;
import scw.sql.orm.enums.OperationType;
import scw.util.attribute.SimpleAttributes;

@SuppressWarnings("serial")
public final class GeneratorContext extends SimpleAttributes<Object, Object> {
	private MappingContext mappingContext;
	private final ORMOperations ormOperations;
	private final OperationType operationType;
	private final Object bean;
	private final SqlMapper sqlMapper;
	private final String tableName;// ORM 入参，并非实际表名

	protected GeneratorContext(ORMOperations ormOperations, OperationType operationType, Object bean,
			SqlMapper sqlMapper, String tableName) {
		this.ormOperations = ormOperations;
		this.operationType = operationType;
		this.bean = bean;
		this.sqlMapper = sqlMapper;
		this.tableName = tableName;
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

	protected final void setMappingContext(MappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	public final SqlMapper getSqlMapper() {
		return sqlMapper;
	}

	/**
	 * 入参，并非实际表名
	 * 
	 * @return
	 */
	public final String getTableName() {
		return tableName;
	}
}
