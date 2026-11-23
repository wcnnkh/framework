package run.soeasy.framework.data.orm.annotation;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.resolver.EntityResolver;

public class AnnotationEntityResolver implements EntityResolver {

	@Override
	public String getEntityName(@NonNull TypeDescriptor entityDescriptor) {
		Entity entity = entityDescriptor.getAnnotation(Entity.class);
		if (entity != null && StringUtils.isNotEmpty(entity.name())) {
			return entity.name();
		}
		return entityDescriptor.getType().getSimpleName();
	}
}
