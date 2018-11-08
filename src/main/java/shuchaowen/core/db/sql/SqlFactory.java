package shuchaowen.core.db.sql;

public interface SqlFactory{
	SQL getSql(String name, Object ...params);
}
