package run.soeasy.framework.data.orm;

import java.util.concurrent.ConcurrentSkipListSet;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.CollectionContainer;
import run.soeasy.framework.core.exchange.Registry;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.mapping.MappingContext;
import run.soeasy.framework.core.mapping.MappingFilter;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.spi.Configurable;
import run.soeasy.framework.core.spi.ServiceDiscoverer;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.orm.resolver.ConfigurableEntityResolver;
import run.soeasy.framework.data.orm.resolver.EntityResolver;

@Getter
public class ObjectRelationalMapper extends BeanMapper implements Configurable, EntityResolver {
	private final ConfigurableEntityResolver entityResolver = new ConfigurableEntityResolver();
	private final Registry<Class<?>> entityRegistry = new CollectionContainer<>(new ConcurrentSkipListSet<>());

	public ObjectRelationalMapper() {
		this.configure();
	}

	@Override
	public Signal configure(@NonNull ServiceDiscoverer discovery) {
		return entityResolver.configure(discovery);
	}

	@Override
	public boolean isEntity(@NonNull TypeDescriptor typeDescriptor) {
		if (entityRegistry.contains(typeDescriptor.getType())) {
			return true;
		}

		return entityResolver.isEntity(typeDescriptor);
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnAlias(typeDescriptor, propertyDescriptor);
	}

	@Override
	public String getColumnName(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnName(typeDescriptor, propertyDescriptor);
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor) {
		return entityResolver.getEntityAlias(typeDescriptor);
	}

	@Override
	public String getEntityName(@NonNull TypeDescriptor typeDescriptor) {
		return entityResolver.getEntityName(typeDescriptor);
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.isExclude(typeDescriptor, propertyDescriptor);
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.isPrimaryKey(typeDescriptor, propertyDescriptor);
	}

	@Override
	protected boolean doFilterableMapping(
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> sourceContext,
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> targetContext,
			@NonNull Iterable<MappingFilter<String, PropertyAccessor, PropertyMapping<PropertyAccessor>>> filters) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return super.doFilterableMapping(sourceContext, targetContext, filters);
		}

		// TODO 实现支持别名的映射
		return super.doFilterableMapping(sourceContext, targetContext, filters);
	}

	public EntityDescriptor<PropertyDescriptor> getEntityDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		if (!isEntity(typeDescriptor)) {
			throw new IllegalArgumentException("This is not an entity:" + typeDescriptor);
		}
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getObjectTemplate(typeDescriptor.getType()).elements()
						.filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	public EntityDescriptor<PropertyAccessor> getEntityDescriptor(@NonNull TypeDescriptor typeDescriptor,
			@NonNull Object entity) {
		if (!isEntity(typeDescriptor)) {
			throw new IllegalArgumentException("This is not an entity:" + typeDescriptor);
		}
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getMapping(entity, typeDescriptor).elements().filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	protected <T extends PropertyDescriptor> ColumnDescriptor<T> toColumn(@NonNull TypeDescriptor typeDescriptor,
			T property) {
		return new ColumnDescriptor<>(property, getColumnName(typeDescriptor, property),
				isPrimaryKey(typeDescriptor, property), getColumnAlias(typeDescriptor, property));
	}
}
