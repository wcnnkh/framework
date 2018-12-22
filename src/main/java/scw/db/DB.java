package scw.db;

import scw.db.sql.SQLFormat;

public abstract class DB extends AbstractDB {
	public DB() {
		this(null);
	}

	public DB(SQLFormat sqlFormat) {
		super(sqlFormat);
	}
}