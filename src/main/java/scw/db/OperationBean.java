package scw.db;

import java.io.Serializable;

import scw.database.DataBaseUtils;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.db.sql.SQLFormat;

public final class OperationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object bean;
	private OperationType operationType;
	private String tableName;

	protected OperationBean() {
	};// 用于序列化

	public OperationBean(OperationType operationType, Object bean, String tableName) {
		this.bean = bean;
		this.operationType = operationType;
		this.tableName = tableName;
	}

	public OperationBean(OperationType operationType, Object bean) {
		this(operationType, bean, null);
	}

	public Object getBean() {
		return bean;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public String getTableName() {
		return tableName;
	}

	public SQL getSql(SQLFormat sqlFormat) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(bean.getClass());
		String tName;
		if (tableName == null || tableName.length() == 0) {
			if (bean instanceof TableName) {
				tName = ((TableName) bean).tableName();
			} else {
				tName = tableInfo.getName();
			}
		} else {
			tName = tableName;
		}

		switch (operationType) {
		case SAVE:
			return sqlFormat.toInsertSql(bean, tableInfo, tName);
		case UPDATE:
			return sqlFormat.toUpdateSql(bean, tableInfo, tName);
		case DELETE:
			return sqlFormat.toDeleteSql(bean, tableInfo, tName);
		case SAVE_OR_UPDATE:
			return sqlFormat.toSaveOrUpdateSql(bean, tableInfo, tName);
		default:
			return null;
		}
	}
}
