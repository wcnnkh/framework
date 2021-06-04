package scw.orm.sql.convert;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.sql.RowMapper;

/**
 * @see ResultSetRowToEntityConversionService
 * @author shuchaowen
 *
 * @param <T>
 */
public class SimpleRowMapper<T> implements RowMapper<T> {
	private final ConversionService conversionService;
	private final Class<? extends T> clazz;

	public SimpleRowMapper(Class<? extends T> clazz) {
		this(Sys.env.getConversionService(), clazz);
	}

	public SimpleRowMapper(ConversionService conversionService, Class<? extends T> clazz) {
		this.conversionService = conversionService;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		return (T) conversionService.convert(rs, TypeDescriptor.forObject(rs), TypeDescriptor.valueOf(clazz));
	}

}
