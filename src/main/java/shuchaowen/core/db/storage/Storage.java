package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.PrimaryKeyParameter;

public interface Storage {
	<T> T getById(Class<T> type, Object... params);

	<T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	void save(Collection<?> beans);

	void update(Collection<?> beans);

	void delete(Collection<?> beans);

	void saveOrUpdate(Collection<?> beans);
}
