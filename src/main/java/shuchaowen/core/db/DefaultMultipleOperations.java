package shuchaowen.core.db;

import java.io.Serializable;
import java.util.ArrayList;

public class DefaultMultipleOperations extends ArrayList<OperationBean> implements MultipleOperations, Serializable{
	private static final long serialVersionUID = 1L;
	
	public MultipleOperations save(Object... beans) {
		for(Object bean : beans){
			if(bean == null){
				continue;
			}
			
			add(new OperationBean(OperationType.SAVE, bean));
		}
		return this;
	}

	public MultipleOperations update(Object... beans) {
		for(Object bean : beans){
			if(bean == null){
				continue;
			}
			
			add(new OperationBean(OperationType.UPDATE, bean));
		}
		return this;
	}

	public MultipleOperations delete(Object... beans) {
		for(Object bean : beans){
			if(bean == null){
				continue;
			}
			
			add(new OperationBean(OperationType.DELETE, bean));
		}
		return this;
	}

	public MultipleOperations saveOrUpdate(Object... beans) {
		for(Object bean : beans){
			if(bean == null){
				continue;
			}
			
			add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean));
		}
		return this;
	}

	public void commit(DB db) {
		if(isEmpty()){
			return ;
		}
		db.op(this);
	}
}
