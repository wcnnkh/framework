package shuchaowen.db.id.factory;

public interface IdGeneratorFactory {
	Long next(Class<?> tableClass);
}
