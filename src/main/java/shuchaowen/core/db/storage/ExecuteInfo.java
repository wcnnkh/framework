package shuchaowen.core.db.storage;

import java.io.Serializable;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.SQL;

public final class ExecuteInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private EOperationType operationType;
	private Collection<?> beanList;
	
	public ExecuteInfo(){};
	
	public ExecuteInfo(EOperationType operationType, Collection<?> beans){
		this.operationType = operationType;
		this.beanList = beans;
	}
	
	public EOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(EOperationType operationType) {
		this.operationType = operationType;
	}

	public Collection<?> getBeanList() {
		return beanList;
	}

	public void setBeanList(Collection<?> beanList) {
		this.beanList = beanList;
	}
	
	public Collection<SQL> getSqlList(AbstractDB abstractDB){
		if(beanList == null){
			return null;
		}
		
		switch (operationType) {
		case SAVE:
			return abstractDB.getSaveSqlList(beanList);
		case DELETE:
			return abstractDB.getDeleteSqlList(beanList);
		case UPDATE:
			return abstractDB.getUpdateSqlList(beanList);
		case SAVE_OR_UPDATE:
			return abstractDB.getSaveOrUpdateSqlList(beanList);
		default:
			break;
		}
		return null;
	}
}
