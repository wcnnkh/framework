package run.soeasy.framework.data.orm.resolver;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
final class EntityResolverChain implements EntityResolver {
	private final Iterator<EntityResolverExtension> iterator;
	private final EntityResolver next;

	@Override
	public String getEntityName(@NonNull TypeDescriptor entityDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getEntityName(entityDescriptor, this);
		} else if (next != null) {
			return next.getEntityName(entityDescriptor);
		}
		return null;
	}
}
