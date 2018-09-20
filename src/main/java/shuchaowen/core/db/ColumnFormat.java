package shuchaowen.core.db;

public interface ColumnFormat {
	Object get(Object obj, ColumnInfo columnInfo) throws Throwable;
	
	void set(Object obj, ColumnInfo columnInfo, Object value) throws Throwable;
}
