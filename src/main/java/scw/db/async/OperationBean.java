package scw.db.async;

import java.io.Serializable;

import scw.common.exception.NotSupportException;
import scw.sql.Sql;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;

public final class OperationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String tableName;
	private Object bean;
	private OperationType operationType;

	/**
	 * 用于序列化
	 */
	protected OperationBean() {
	};

	public OperationBean(OperationType operationType, Object bean) {
		this(operationType, bean, null);
	}

	public OperationBean(OperationType operationType, Object bean, String tableName) {
		this.operationType = operationType;
		this.bean = bean;
		this.tableName = tableName;
	}

	public Sql format(SqlFormat sqlFormat) {
		if (bean == null) {
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		switch (operationType) {
		case SAVE:
			return sqlFormat.toInsertSql(bean, tableInfo, tName);
		case DELETE:
			return sqlFormat.toDeleteSql(bean, tableInfo, tName);
		case UPDATE:
			return sqlFormat.toUpdateSql(bean, tableInfo, tName);
		case SAVE_OR_UPDATE:
			return sqlFormat.toSaveOrUpdateSql(bean, tableInfo, tName);
		default:
			throw new NotSupportException("不支持ORM操作类型：" + operationType);
		}
	}
}
