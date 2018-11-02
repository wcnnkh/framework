package shuchaowen.core.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class DefaultMultipleOperations implements MultipleOperations, Serializable{
	private static final long serialVersionUID = 1L;
	private ArrayList<OperationBean> list;
	
	private void checkList(){
		if(list == null){
			list = new ArrayList<OperationBean>();
		}
	}
	
	public MultipleOperations save(Object... beans) {
		checkList();
		for(Object bean : beans){
			list.add(new OperationBean(OperationType.SAVE, bean));
		}
		return this;
	}

	public MultipleOperations update(Object... beans) {
		checkList();
		for(Object bean : beans){
			list.add(new OperationBean(OperationType.UPDATE, bean));
		}
		return this;
	}

	public MultipleOperations delete(Object... beans) {
		checkList();
		for(Object bean : beans){
			list.add(new OperationBean(OperationType.DELETE, bean));
		}
		return this;
	}

	public MultipleOperations saveOrUpdate(Object... beans) {
		checkList();
		for(Object bean : beans){
			list.add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean));
		}
		return this;
	}

	public Collection<OperationBean> getOperationBeans() {
		return list;
	}

}
