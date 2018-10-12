package shuchaowen.core.db.storage;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;

public abstract class AbstractExecuteStorage extends AbstractStorage{
	public AbstractExecuteStorage(AbstractDB db, SQLFormat sqlFormat) {
		super(db, sqlFormat);
	}
}
