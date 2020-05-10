package scw.db.async;

import scw.sql.orm.enums.OperationType;

public final class BeanAsyncExecute extends AsyncExecute {
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

	public Object call() throws Exception {
		switch (operationType) {
		case SAVE:
			getDb().save(bean, tableName);
			break;
		case DELETE:
			getDb().delete(bean, tableName);
			break;
		case UPDATE:
			getDb().update(bean, tableName);
		case SAVE_OR_UPDATE:
			getDb().saveOrUpdate(bean, tableName);
		default:
			break;
		}
		return null;
	}
}
