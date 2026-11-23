package run.soeasy.framework.data.orm.resolver;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;

@RequiredArgsConstructor
final class PrimaryKeyResolverChain implements PrimaryKeyResolver {
	private final Iterator<PrimaryKeyResolverExtension> iterator;
	private final PrimaryKeyResolver chain;

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityDescriptor, propertyDescriptor, this);
		} else if (chain != null) {
			return chain.isPrimaryKey(entityDescriptor, propertyDescriptor);
		}
		return false;
	}
}
