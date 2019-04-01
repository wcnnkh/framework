package scw.db.auto;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public interface AutoCreateService {
	
	void wrapper(Object bean, TableInfo tableInfo, ColumnInfo columnInfo, String tableName);
	
}
