package scw.db;

import scw.sql.orm.enums.OperationType;

public final class BeanAsyncExecute implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private final Object bean;
	private final String tableName;
	private final OperationType operationType;

	public BeanAsyncExecute(Object bean, OperationType operationType) {
		this(bean, null, operationType);
	}

	public BeanAsyncExecute(Object bean, String tableName, OperationType operationType) {
		this.bean = bean;
		this.tableName = tableName;
		this.operationType = operationType;
	}

	public void execute(DB db) {
		switch (operationType) {
		case SAVE:
			db.save(bean, tableName);
			break;
		case DELETE:
			db.delete(bean, tableName);
			break;
		case UPDATE:
			db.update(bean, tableName);
		case SAVE_OR_UPDATE:
			db.saveOrUpdate(bean, tableName);
		default:
			break;
		}
	}
}
