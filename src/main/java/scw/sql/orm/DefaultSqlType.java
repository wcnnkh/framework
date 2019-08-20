package scw.sql.orm;

import java.io.Serializable;

public class DefaultSqlType implements SqlType, Serializable{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final long length;

	public DefaultSqlType(String name, long length) {
		this.name = name;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public long getLength() {
		return length;
	}

}
