package run.soeasy.framework.data.orm.annotation;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
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

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor entityDescriptor) {
		Entity entity = entityDescriptor.getAnnotation(Entity.class);
		if (entity != null && entity.alias().length > 0) {
			return Streamable.array(entity.alias());
		}
		return Streamable.empty();
	}

}
