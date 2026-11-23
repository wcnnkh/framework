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
import run.soeasy.framework.data.orm.annotation.AnnotationColumnResolver;
import run.soeasy.framework.data.orm.naming.CamelCase;
import run.soeasy.framework.data.orm.naming.NamingStrategy;

@Getter
@Setter
public class ConfigurableColumnResolver extends ConfigurableServices<ColumnResolverExtension>
		implements ColumnResolver {
	@NonNull
	private ColumnResolver defaultColumnResolver = new AnnotationColumnResolver();
	@NonNull
	private NamingStrategy namingStrategy = CamelCase.INSTANCE;

	public ConfigurableColumnResolver() {
		super(ServiceComparator.defaultServiceComparator());
	}

	@Override
	public String getColumnName(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		ColumnResolver chain = new ColumnResolverChain(iterator(), defaultColumnResolver);
		String name = chain.getColumnName(entityDescriptor, propertyDescriptor);
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		name = propertyDescriptor.getName();
		name = namingStrategy.apply(name);
		return name;
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		ColumnResolver chain = new ColumnResolverChain(iterator(), defaultColumnResolver);
		return chain.getColumnAlias(entityDescriptor, propertyDescriptor);
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		ColumnResolver chain = new ColumnResolverChain(iterator(), defaultColumnResolver);
		return chain.isExclude(entityDescriptor, propertyDescriptor);
	}

}
