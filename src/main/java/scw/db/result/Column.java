package scw.db.result;

import java.io.Serializable;

public class Column implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String tableName;
	
	protected Column(){};
	
	protected Column(String name, String tableName){
		this.name = name;
		this.tableName = tableName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
}
