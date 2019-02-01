package scw.db;

import scw.db.sql.SqlFormat;

public abstract class DB extends AbstractDB {
	public DB() {
		this(null);
	}

	public DB(SqlFormat sqlFormat) {
		super(sqlFormat);
	}
}