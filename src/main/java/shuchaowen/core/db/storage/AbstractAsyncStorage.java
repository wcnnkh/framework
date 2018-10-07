package shuchaowen.core.db.storage;

import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

/**
 * 实现异步保存到数据库
 * @author asus1
 *
 */
public abstract class AbstractAsyncStorage extends DefaultStorage implements IAsyncStorage{
	@Override
	public void save(Collection<Object> beans, AbstractDB db,
			SQLFormat sqlFormat) {
		producer(new ExecuteInfo(db.getClass(), sqlFormat.getClass(), EOperationType.SAVE, beans));
	}
	
	@Override
	public void delete(Collection<Object> beans, AbstractDB db,
			SQLFormat sqlFormat) {
		producer(new ExecuteInfo(db.getClass(), sqlFormat.getClass(), EOperationType.DELETE, beans));
	}
	
	@Override
	public void update(Collection<Object> beans, AbstractDB db,
			SQLFormat sqlFormat) {
		producer(new ExecuteInfo(db.getClass(), sqlFormat.getClass(), EOperationType.UPDATE, beans));
	}
	
	@Override
	public void saveOrUpdate(Collection<Object> beans, AbstractDB db,
			SQLFormat sqlFormat) {
		producer(new ExecuteInfo(db.getClass(), sqlFormat.getClass(), EOperationType.SAVE_OR_UPDATE, beans));
	}
	
	protected Collection<SQL> getSqlList(ExecuteInfo executeInfo){
		switch (executeInfo.getOperationType()) {
		case SAVE:
			return getSaveSqlList(executeInfo.getBeanList(), executeInfo.getSQLFormat());
		case DELETE:
			return getDeleteSqlList(executeInfo.getBeanList(), executeInfo.getSQLFormat());
		case UPDATE:
			return getUpdateSqlList(executeInfo.getBeanList(), executeInfo.getSQLFormat());
		case SAVE_OR_UPDATE:
			return getSaveOrUpdateSqlList(executeInfo.getBeanList(), executeInfo.getSQLFormat());
		default:
			break;
		}
		throw new ShuChaoWenRuntimeException("not found OperationType [" + executeInfo.getOperationType() + "]");
	}
}
