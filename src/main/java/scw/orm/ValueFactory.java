package scw.orm;

public interface ValueFactory {
	Object getValue(FieldDefinitionContext context, ORMOperations ormOperations);
}
