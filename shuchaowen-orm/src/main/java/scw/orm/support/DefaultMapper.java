package scw.orm.support;

import java.util.Collection;
import java.util.Map;

import scw.orm.AbstractMapper;
import scw.orm.Column;
import scw.orm.ColumnFactory;
import scw.orm.GetterFilter;
import scw.orm.ORMInstanceFactory;
import scw.orm.ORMUtils;
import scw.orm.SetterFilter;

public class DefaultMapper extends AbstractMapper {
	private final ColumnFactory columnFactory;
	private final Collection<? extends SetterFilter> setterFilters;
	private final Collection<? extends GetterFilter> getterFilters;
	private final ORMInstanceFactory instanceFactory;

	public DefaultMapper(ColumnFactory columnFactory, Collection<? extends SetterFilter> setterFilters,
			Collection<? extends GetterFilter> getterFilters, ORMInstanceFactory instanceFactory) {
		this.columnFactory = columnFactory;
		this.setterFilters = setterFilters;
		this.getterFilters = getterFilters;
		this.instanceFactory = instanceFactory;
	}

	@Override
	public char getPrimaryKeyConnectorCharacter() {
		return ORMUtils.PRIMARY_KEY_CONNECTOR_CHARACTER;
	}
	
	public ORMInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Map<String, ? extends Column> getColumnMap(Class<?> clazz) {
		return columnFactory.getColumnMap(clazz);
	}

	@Override
	public Collection<? extends SetterFilter> getSetterFilters() {
		return setterFilters;
	}

	@Override
	public Collection<? extends GetterFilter> getGetterFilters() {
		return getterFilters;
	}
}
