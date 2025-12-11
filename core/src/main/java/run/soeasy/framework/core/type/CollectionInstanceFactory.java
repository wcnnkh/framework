package run.soeasy.framework.core.type;

import java.util.Collection;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;

public class CollectionInstanceFactory implements InstanceFactory {

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		Class<?> type = requiredType.getRawType();
		return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		Class<?> type = requiredType.getRawType();
		if (Collection.class.isAssignableFrom(type)) {
			return CollectionUtils.createCollection(type);
		} else if (Map.class.isAssignableFrom(type)) {
			return CollectionUtils.createMap(type);
		}
		throw new UnsupportedOperationException("无法创建 [" + requiredType + "] 类型的集合");
	}

}
