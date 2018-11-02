package shuchaowen.core.db;

import java.io.Serializable;

import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;

public class OperationBean implements Serializable{
	private static final long serialVersionUID = 3110383969159251825L;
	private Object bean;
	private OperationType operationType;
	
	public OperationBean(){};//用于序列化
	
	public OperationBean(OperationType operationType, Object bean){
		this.bean = bean;
		this.operationType = operationType;
	}
	
	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public OperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	
	public SQL getSql(SQLFormat sqlFormat){
		switch (operationType) {
		case SAVE:
			return sqlFormat.toInsertSql(bean);
		case UPDATE:
			return sqlFormat.toUpdateSql(bean);
		case DELETE:
			return sqlFormat.toDeleteSql(bean);
		case SAVE_OR_UPDATE:
			return sqlFormat.toSaveOrUpdateSql(bean);
		default:
			return null;
		}
	}
}
