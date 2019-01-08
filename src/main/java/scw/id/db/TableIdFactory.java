package scw.id.db;

public interface TableIdFactory {
	long generator(Class<?> tableClass, String fieldName);
}
