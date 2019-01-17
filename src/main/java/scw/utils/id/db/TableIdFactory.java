package scw.utils.id.db;

public interface TableIdFactory {
	long generator(Class<?> tableClass, String fieldName);
}
