package run.soeasy.framework.data.orm.resolver;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

@RequiredArgsConstructor
final class EntityResolverChain implements EntityResolver {
	private final Iterator<EntityResolverExtension> iterator;
	private final EntityResolver next;

	@Override
	public String getEntityName(@NonNull TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getEntityName(typeDescriptor, this);
		} else if (next != null) {
			return next.getEntityName(typeDescriptor);
		}
		return null;
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getEntityAlias(typeDescriptor, this);
		} else if (next != null) {
			return next.getEntityAlias(typeDescriptor);
		}
		return Streamable.empty();
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getColumnAlias(propertyDescriptor, this);
		} else if (next != null) {
			return next.getColumnAlias(propertyDescriptor);
		}
		return Streamable.empty();
	}

	@Override
	public String getColumnName(@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getColumnName(propertyDescriptor, this);
		} else if (next != null) {
			return next.getColumnName(propertyDescriptor);
		}
		return null;
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isExclude(typeDescriptor, propertyDescriptor, this);
		} else if (next != null) {
			return next.isExclude(typeDescriptor, propertyDescriptor);
		}
		return false;
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(typeDescriptor, propertyDescriptor, this);
		} else if (next != null) {
			return next.isPrimaryKey(typeDescriptor, propertyDescriptor);
		}
		return false;
	}
}
