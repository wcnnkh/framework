package scw.sqlite.test.orm;

import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.annotation.Table;

@Table
public class TestTable1{
	@PrimaryKey
	private int id;
	private String key;
	private int value;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
