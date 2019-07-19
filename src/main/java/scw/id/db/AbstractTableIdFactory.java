package scw.id.db;

import scw.core.exception.NotFoundException;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class AbstractTableIdFactory implements TableIdFactory {
	private final ORMOperations db;

	public AbstractTableIdFactory(ORMOperations db) {
		this.db = db;
	}

	protected long getMaxId(Class<?> tableClass, String fieldName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		ColumnInfo columnInfo = tableInfo.getColumnInfo(fieldName);
		if (columnInfo == null) {
			throw new NotFoundException(fieldName);
		}

		Number maxNumber = (Number) db.getMaxValue(tableClass, tableInfo.getDefaultName(), fieldName);
		return maxNumber == null ? 0 : maxNumber.longValue();
	}
}
