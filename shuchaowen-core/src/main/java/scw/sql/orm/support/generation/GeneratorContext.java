package scw.sql.orm.support.generation;

import scw.sql.orm.Column;
import scw.sql.orm.EntityOperations;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.enums.OperationType;
import scw.util.attribute.SimpleAttributes;

@SuppressWarnings("serial")
public final class GeneratorContext extends SimpleAttributes<Object, Object> {
	private Column column;
	private final EntityOperations entityOperations;
	private final OperationType operationType;
	private final Object bean;
	private final ObjectRelationalMapping objectRelationalMapping;
	private final String tableName;// ORM 入参，并非实际表名

	public GeneratorContext(EntityOperations entityOperations, OperationType operationType, Object bean,
			ObjectRelationalMapping objectRelationalMapping, String tableName) {
		this.entityOperations = entityOperations;
		this.operationType = operationType;
		this.bean = bean;
		this.objectRelationalMapping = objectRelationalMapping;
		this.tableName = tableName;
	}

	public final OperationType getOperationType() {
		return operationType;
	}

	public final Object getBean() {
		return bean;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public EntityOperations getEntityOperations() {
		return entityOperations;
	}

	public ObjectRelationalMapping getObjectRelationalMapping() {
		return objectRelationalMapping;
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
