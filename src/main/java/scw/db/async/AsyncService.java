package scw.db.async;

import scw.sql.Sql;

public interface AsyncService {
	void asyncSave(Object... objs);

	void asyncUpdate(Object... objs);

	void asyncDelete(Object... objs);

	void asyncSaveOrUpdate(Object... objs);

	void asyncExecute(OperationBean... operationBeans);

	void asyncExecute(MultipleOperation multipleOperation);

	void asyncExecute(Sql... sqls);
}
