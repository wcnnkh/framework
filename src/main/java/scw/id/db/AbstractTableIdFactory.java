package scw.id.db;

import scw.common.exception.NotFoundException;
import scw.database.ColumnInfo;
import scw.database.DataBaseUtils;
import scw.database.TableInfo;
import scw.db.DB;

public abstract class AbstractTableIdFactory implements TableIdFactory {
	private final DB db;

	public AbstractTableIdFactory(DB db) {
		this.db = db;
	}

	protected long getMaxId(Class<?> tableClass, String fieldName) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		ColumnInfo columnInfo = tableInfo.getColumnInfo(fieldName);
		if (columnInfo == null) {
			throw new NotFoundException(fieldName);
		}

		Number maxNumber = (Number) db.getMaxValue(columnInfo.getType(), tableClass, tableInfo.getName(), fieldName);
		return maxNumber == null ? 0 : maxNumber.longValue();
	}
}
