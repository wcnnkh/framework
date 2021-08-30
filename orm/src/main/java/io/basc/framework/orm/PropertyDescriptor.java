package io.basc.framework.orm;

public interface PropertyDescriptor {
	String getName();
	
	boolean isPrimaryKey();
	
	boolean isNullable();
}
