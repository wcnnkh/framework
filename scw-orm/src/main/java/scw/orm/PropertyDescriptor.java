package scw.orm;

public interface PropertyDescriptor {
	String getName();
	
	boolean isPrimaryKey();
	
	boolean isNullable();
}
