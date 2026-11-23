package run.soeasy.framework.data.orm.resolver;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;
import run.soeasy.framework.data.orm.annotation.AnnotationPrimaryKeyResolver;

@Getter
@Setter
public class ConfigurablePrimaryKeyResolver extends ConfigurableServices<PrimaryKeyResolverExtension>
		implements PrimaryKeyResolver {
	@NonNull
	private PrimaryKeyResolver defaultPrimaryKeyResolver = new AnnotationPrimaryKeyResolver();

	public ConfigurablePrimaryKeyResolver() {
		super(ServiceComparator.defaultServiceComparator());
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		PrimaryKeyResolver chain = new PrimaryKeyResolverChain(iterator(), defaultPrimaryKeyResolver);
		return chain.isPrimaryKey(entityDescriptor, propertyDescriptor);
	}
}
