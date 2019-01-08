package scw.id.db;

public interface TableIdFactory {
	Long generator(Class<?> tableClass, String fieldName);
}
