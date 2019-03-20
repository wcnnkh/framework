package scw.sql.async;

import scw.sql.SqlOperations;
import scw.sql.orm.MultipleOperation;
import scw.sql.orm.OperationBean;
import scw.sql.orm.SqlFormat;

public abstract class AbstractAsyncService implements AsyncService {
	protected final SqlOperations sqlOperations;
	protected final SqlFormat sqlFormat;

	public AbstractAsyncService(SqlOperations sqlOperations, SqlFormat sqlFormat) {
		this.sqlOperations = sqlOperations;
		this.sqlFormat = sqlFormat;
	}

	public void save(Object... objs) {
		MultipleOperation operation = new MultipleOperation();
		for (Object bean : objs) {
			operation.save(bean);
		}
		execute(operation.format(sqlFormat));
	}

	public void update(Object... objs) {
		MultipleOperation operation = new MultipleOperation();
		for (Object bean : objs) {
			operation.update(bean);
		}
		execute(operation.format(sqlFormat));
	}

	public void delete(Object... objs) {
		MultipleOperation operation = new MultipleOperation();
		for (Object bean : objs) {
			operation.delete(bean);
		}
		execute(operation.format(sqlFormat));
	}

	public void saveOrUpdate(Object... objs) {
		MultipleOperation operation = new MultipleOperation();
		for (Object bean : objs) {
			operation.saveOrUpdate(bean);
		}
		execute(operation.format(sqlFormat));
	}

	public void execute(OperationBean... operationBeans) {
		MultipleOperation operation = new MultipleOperation();
		for (OperationBean bean : operationBeans) {
			operation.add(bean);
		}
		execute(operation.format(sqlFormat));
	}

	public SqlFormat getSqlFormat() {
		return sqlFormat;
	}
}
