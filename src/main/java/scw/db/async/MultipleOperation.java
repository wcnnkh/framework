package scw.db.async;

import java.util.LinkedList;

import scw.sql.Sql;
import scw.sql.Sqls;
import scw.sql.orm.SqlFormat;

public final class MultipleOperation extends LinkedList<OperationBean> {
	private static final long serialVersionUID = 1L;

	public void save(Object bean, String tableName) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.SAVE, bean, tableName));
	}

	public void update(Object bean, String tableName) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.UPDATE, bean, tableName));
	}

	public void delete(Object bean, String tableName) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.DELETE, bean, tableName));
	}

	public void saveOrUpdate(Object bean, String tableName) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean, tableName));
	}

	public void save(Object bean) {
		add(new OperationBean(OperationType.SAVE, bean, null));
	}

	public void update(Object bean) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.UPDATE, bean, null));
	}

	public void delete(Object bean) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.DELETE, bean, null));
	}

	public void saveOrUpdate(Object bean) {
		if(bean == null){
			return ;
		}
		
		add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean, null));
	}

	public Sqls format(SqlFormat sqlFormat) {
		Sqls sqls = new Sqls();
		for (OperationBean operationBean : this) {
			if (operationBean == null) {
				continue;
			}

			Sql sql = operationBean.format(sqlFormat);
			if (sql == null) {
				continue;
			}

			sqls.add(sql);
		}
		return sqls;
	}
}
