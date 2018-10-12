package shuchaowen.core.db.storage;

import java.io.Serializable;
import java.util.Collection;

public final class ExecuteInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private EOperationType operationType;
	private Collection<Object> beanList;
	
	public ExecuteInfo(){};
	
	public ExecuteInfo(EOperationType operationType, Collection<Object> beans){
		this.operationType = operationType;
		this.beanList = beans;
	}
	
	public EOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(EOperationType operationType) {
		this.operationType = operationType;
	}

	public Collection<Object> getBeanList() {
		return beanList;
	}

	public void setBeanList(Collection<Object> beanList) {
		this.beanList = beanList;
	}
}
