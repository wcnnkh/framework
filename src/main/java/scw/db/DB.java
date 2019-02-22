package scw.db;

import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.Cache;

public abstract class DB extends AbstractDB {
	public DB() {
		this(null);
	}

	public DB(SqlFormat sqlFormat) {
		super(sqlFormat, null);
	}

	public DB(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat, cache);
	}
}