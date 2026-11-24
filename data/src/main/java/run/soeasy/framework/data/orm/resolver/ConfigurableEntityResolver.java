package run.soeasy.framework.data.orm.resolver;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.annotation.AnnotationEntityResolver;
import run.soeasy.framework.data.orm.naming.CamelCase;
import run.soeasy.framework.data.orm.naming.NamingStrategy;

@Getter
@Setter
public class ConfigurableEntityResolver extends ConfigurableServices<EntityResolverExtension>
		implements EntityResolver {
	@NonNull
	private EntityResolver defaultEntityResolver = new AnnotationEntityResolver();
	@NonNull
	private NamingStrategy namingStrategy = CamelCase.INSTANCE;

	public ConfigurableEntityResolver() {
		super(ServiceComparator.defaultServiceComparator());
	}

	@Override
	public String getEntityName(@NonNull TypeDescriptor typeDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		String name = chain.getEntityName(typeDescriptor);
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}
		name = typeDescriptor.getType().getSimpleName();
		name = namingStrategy.apply(name);
		return name;
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		return chain.getEntityAlias(typeDescriptor);
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		return chain.isPrimaryKey(typeDescriptor, propertyDescriptor);
	}

	@Override
	public String getColumnName(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		String name = chain.getColumnName(typeDescriptor, propertyDescriptor);
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		name = propertyDescriptor.getName();
		name = namingStrategy.apply(name);
		return name;
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		return chain.getColumnAlias(typeDescriptor, propertyDescriptor);
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		return chain.isExclude(typeDescriptor, propertyDescriptor);
	}

	@Override
	public boolean isEntity(@NonNull TypeDescriptor typeDescriptor) {
		EntityResolverChain chain = new EntityResolverChain(iterator(), defaultEntityResolver);
		return chain.isEntity(typeDescriptor);
	}
}
