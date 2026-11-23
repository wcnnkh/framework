package run.soeasy.framework.data.orm.resolver;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

@RequiredArgsConstructor
final class ColumnResolverChain implements ColumnResolver {
	private final Iterator<ColumnResolverExtension> iterator;
	private final ColumnResolver chain;

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getColumnAlias(entityDescriptor, propertyDescriptor, this);
		} else if (chain != null) {
			return chain.getColumnAlias(entityDescriptor, propertyDescriptor);
		}
		return Streamable.empty();
	}

	@Override
	public String getColumnName(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getColumnName(entityDescriptor, propertyDescriptor, this);
		} else if (chain != null) {
			return chain.getColumnName(entityDescriptor, propertyDescriptor);
		}
		return null;
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isExclude(entityDescriptor, propertyDescriptor, this);
		} else if (chain != null) {
			return chain.isExclude(entityDescriptor, propertyDescriptor);
		}
		return false;
	}
}
