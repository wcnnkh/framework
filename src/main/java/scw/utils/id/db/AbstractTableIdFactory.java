package scw.utils.id.db;

import scw.core.exception.NotFoundException;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SelectMaxId;
import scw.sql.orm.TableInfo;

public abstract class AbstractTableIdFactory implements TableIdFactory {
	private final SelectMaxId db;

	public AbstractTableIdFactory(SelectMaxId db) {
		this.db = db;
	}

	protected long getMaxId(Class<?> tableClass, String fieldName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		ColumnInfo columnInfo = tableInfo.getColumnInfo(fieldName);
		if (columnInfo == null) {
			throw new NotFoundException(fieldName);
		}

		Number maxNumber = (Number) db.getMaxValue(columnInfo.getType(), tableClass, tableInfo.getName(), fieldName);
		return maxNumber == null ? 0 : maxNumber.longValue();
	}
}
