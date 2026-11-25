package run.soeasy.framework.data.orm;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
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

	public ObjectRelationalMapper() {
		this.configure();
	}

	@Override
	public Signal configure(@NonNull ServiceDiscoverer discovery) {
		return entityResolver.configure(discovery);
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnAlias(propertyDescriptor);
	}

	@Override
	public String getColumnName(@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnName(propertyDescriptor);
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
	protected Signal doFilterableMapping(
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
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getObjectTemplate(typeDescriptor.getType()).elements()
						.filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	public EntityDescriptor<PropertyAccessor> getEntityDescriptor(@NonNull TypeDescriptor typeDescriptor,
			@NonNull Object entity) {
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getMapping(entity, typeDescriptor).elements().filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	protected <T extends PropertyDescriptor> ColumnDescriptor<T> toColumn(@NonNull TypeDescriptor typeDescriptor,
			T property) {
		return new ColumnDescriptor<>(property, getColumnName(property), isPrimaryKey(typeDescriptor, property),
				getColumnAlias(property));
	}
}
