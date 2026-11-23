package run.soeasy.framework.data.orm.resolver;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.annotation.AnnotationEntityResolver;
import run.soeasy.framework.data.orm.naming.CamelCase;
import run.soeasy.framework.data.orm.naming.NamingStrategy;

@Getter
@Setter
public class ConfigurableEntityResolver extends ConfigurableServices<EntityResolverExtension> implements EntityResolver {
	@NonNull
	private EntityResolver defaultEntityResolver = new AnnotationEntityResolver();
	@NonNull
	private NamingStrategy namingStrategy = CamelCase.INSTANCE;

	public ConfigurableEntityResolver() {
		super(ServiceComparator.defaultServiceComparator());
	}

	@Override
	public String getEntityName(@NonNull TypeDescriptor entityDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		String name = chain.getEntityName(entityDescriptor);
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}
		name = entityDescriptor.getType().getSimpleName();
		name = namingStrategy.apply(name);
		return name;
	}

}
