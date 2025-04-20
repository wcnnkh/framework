package run.soeasy.framework.core.mapping;

import java.util.EnumSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public final class SystemInstanceFactory extends ConfigurableInstanceFactory {
	private static SystemInstanceFactory instance;

	public static SystemInstanceFactory getInstance() {
		if (instance == null) {
			synchronized (SystemInstanceFactory.class) {
				if (instance == null) {
					instance = new SystemInstanceFactory();
					instance.configure();
				}
			}
		}
		return instance;
	}

	private int collectionInitialCapacity = 16;

	private SystemInstanceFactory() {
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		return super.canInstantiated(requiredType) || requiredType.isCollection() || requiredType.isMap();
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		if (super.canInstantiated(requiredType)) {
			return super.newInstance(requiredType);
		}

		if (requiredType.isCollection()) {
			Class<?> enumClass = null;
			if (EnumSet.class.isAssignableFrom(requiredType.getType())) {
				enumClass = requiredType.getGeneric(0).getType();
			}
			return CollectionUtils.createCollection(requiredType.getType(), enumClass, collectionInitialCapacity);
		}

		if (requiredType.isMap()) {
			return CollectionUtils.createMap(requiredType.getType(), requiredType.getMapKeyTypeDescriptor().getType(),
					collectionInitialCapacity);
		}
		throw new UnsupportedOperationException(requiredType.toString());
	}
}
