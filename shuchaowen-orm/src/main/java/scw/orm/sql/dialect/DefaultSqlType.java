package scw.orm.sql.dialect;

import java.io.Serializable;

public class DefaultSqlType implements SqlType, Serializable{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final int length;

	public DefaultSqlType(String name, int length) {
		this.name = name;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}

}
