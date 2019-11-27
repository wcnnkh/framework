package scw.id.db;

import scw.db.DB;

public abstract class AbstractTableIdFactory implements TableIdFactory {
	private final DB db;

	public AbstractTableIdFactory(DB db) {
		this.db = db;
	}

	protected long getMaxId(Class<?> tableClass, String fieldName) {
		Long value = db.getMaxValue(Long.class, tableClass, fieldName);
		return value == null ? 0 : value.longValue();
	}
}
