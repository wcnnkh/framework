package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.sql.format.SQLFormat;

public interface Storage {
	<T> T getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type, Object ...params);
	
	<T> Map<PrimaryKeyParameter, T> getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters);
	
	<T> List<T> getByIdList(AbstractDB db, SQLFormat sqlFormat, Class<T> type, Object ...params);
	
	void save(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat);
	
	void update(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat);
	
	void delete(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat);
	
	void saveOrUpdate(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat);
}
