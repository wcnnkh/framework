package io.basc.framework.orm.support;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.orm.PropertyMetadata;

public class StandardPropertyMetadata implements PropertyMetadata {
	private String name;
	private boolean autoIncrement;
	private boolean primaryKey;
	private boolean nullable;
	private String charsetName;
	private String comment;
	private boolean unique;

	public StandardPropertyMetadata() {
	}

	public StandardPropertyMetadata(PropertyMetadata propertyMetadata) {
		this.name = propertyMetadata.getName();
		this.autoIncrement = propertyMetadata.isAutoIncrement();
		this.primaryKey = propertyMetadata.isPrimaryKey();
		this.nullable = propertyMetadata.isNullable();
		this.charsetName = propertyMetadata.getCharsetName();
		this.comment = propertyMetadata.getComment();
		this.unique = propertyMetadata.isNullable();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
