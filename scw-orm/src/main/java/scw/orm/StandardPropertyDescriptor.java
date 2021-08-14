package scw.orm;

import scw.mapper.MapperUtils;

public class StandardPropertyDescriptor implements PropertyDescriptor {
	private String name;
	private boolean primaryKey;
	private boolean nullable = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		if (isPrimaryKey()) {
			return false;
		}
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
