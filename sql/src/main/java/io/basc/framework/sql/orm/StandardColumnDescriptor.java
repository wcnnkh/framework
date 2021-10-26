package io.basc.framework.sql.orm;

import io.basc.framework.orm.PropertyDescriptor;
import io.basc.framework.orm.StandardPropertyDescriptor;

public class StandardColumnDescriptor extends StandardPropertyDescriptor
		implements ColumnDescriptor {
	private boolean autoIncrement;
	private boolean unique;
	private String charsetName;
	private String comment;

	public StandardColumnDescriptor() {
	}

	public StandardColumnDescriptor(String name) {
		super(name, false);
	}

	public StandardColumnDescriptor(String name, boolean primaryKey) {
		super(name, primaryKey);
	}

	public StandardColumnDescriptor(
			StandardPropertyDescriptor standardPropertyDescriptor) {
		super(standardPropertyDescriptor);
	}
	
	public StandardColumnDescriptor(
			PropertyDescriptor propertyDescriptor) {
		super(propertyDescriptor);
	}

	public StandardColumnDescriptor(
			StandardColumnDescriptor standardPropertyDescriptor) {
		super(standardPropertyDescriptor);
		this.autoIncrement = standardPropertyDescriptor.autoIncrement;
		this.unique = standardPropertyDescriptor.unique;
		this.charsetName = standardPropertyDescriptor.charsetName;
		this.comment = standardPropertyDescriptor.comment;
	}
	
	public StandardColumnDescriptor(
			ColumnDescriptor columnDescriptor) {
		super(columnDescriptor);
		this.autoIncrement = columnDescriptor.isAutoIncrement();
		this.unique = columnDescriptor.isUnique();
		this.charsetName = columnDescriptor.getCharsetName();
		this.comment = columnDescriptor.getComment();
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
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
}
