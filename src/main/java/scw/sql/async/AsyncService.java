package scw.sql.async;

import scw.sql.Sqls;
import scw.sql.orm.OperationBean;

public interface AsyncService {
	void save(Object... objs);

	void update(Object... objs);

	void delete(Object... objs);

	void saveOrUpdate(Object... objs);

	void execute(OperationBean... operationBeans);

	void execute(Sqls sqls);
}
