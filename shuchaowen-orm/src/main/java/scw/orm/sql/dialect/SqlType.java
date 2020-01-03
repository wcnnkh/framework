package scw.orm.sql.dialect;

public interface SqlType{
	String getName();
	
	/**
	 * 如果长度是0就不处理
	 * @return
	 */
	int getLength();
}
