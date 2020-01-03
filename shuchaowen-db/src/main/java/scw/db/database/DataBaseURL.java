package scw.db.database;

public interface DataBaseURL {

	String getDataBase();

	/**
	 * 获取一个未指定数据库的连接
	 * @return
	 */
	String getConnectionURL();
}
