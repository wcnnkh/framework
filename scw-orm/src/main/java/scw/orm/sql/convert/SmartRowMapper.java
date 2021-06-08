package scw.orm.sql.convert;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.orm.convert.PropertyFactoryToEntityConversionService;
import scw.sql.RowMapper;

/**
 * @see ResultSetPropertyFactory
 * @see PropertyFactoryToEntityConversionService
 * @author shuchaowen
 *
 * @param <T>
 */
public class SmartRowMapper<T> implements RowMapper<T> {
	private final ConversionService conversionService;
	private final Class<? extends T> clazz;

	public SmartRowMapper(Class<? extends T> clazz) {
		this(Sys.env.getConversionService(), clazz);
	}

	public SmartRowMapper(ConversionService conversionService, Class<? extends T> clazz) {
		this.conversionService = conversionService;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetPropertyFactory propertyFactory = new ResultSetPropertyFactory();
		propertyFactory.addResultSet(rs);
		return (T) conversionService.convert(propertyFactory, TypeDescriptor.forObject(propertyFactory),
				TypeDescriptor.valueOf(clazz));
	}

}
