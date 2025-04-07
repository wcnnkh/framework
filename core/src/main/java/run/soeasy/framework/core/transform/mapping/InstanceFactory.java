package run.soeasy.framework.core.transform.mapping;

import java.util.EnumSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.collection.CollectionUtils;

/**
 * 一个实例工厂
 * 
 * @author wcnnkh
 *
 */
public interface InstanceFactory {
	@RequiredArgsConstructor
	public static class CollectionFactory implements InstanceFactory {
		private final int initialCapacity;

		@Override
		public boolean canInstantiated(TypeDescriptor type) {
			return type.isCollection() || type.isMap();
		}

		@Override
		public Object newInstance(TypeDescriptor type) {
			if (type.isCollection()) {
				Class<?> enumClass = null;
				if (EnumSet.class.isAssignableFrom(type.getType())) {
					enumClass = type.getGeneric(0).getType();
				}
				return CollectionUtils.createCollection(type.getType(), enumClass, initialCapacity);
			}

			if (type.isMap()) {
				return CollectionUtils.createMap(type.getType(), type.getMapKeyTypeDescriptor().getType(),
						initialCapacity);
			}
			throw new UnsupportedOperationException(type.toString());
		}

	}

	boolean canInstantiated(@NonNull TypeDescriptor requiredType);

	Object newInstance(@NonNull TypeDescriptor requiredType);
}
