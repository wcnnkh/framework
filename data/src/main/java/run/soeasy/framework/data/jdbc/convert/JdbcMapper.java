package run.soeasy.framework.data.jdbc.convert;

import java.sql.ResultSet;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.data.orm.ObjectRelationalMapper;

public class JdbcMapper extends ObjectRelationalMapper {
	private ConversionService conversionService = new ConversionService();
	
	public JdbcMapper() {
		getMappingProvider().registerFactory(ResultSet.class, (s, e) -> new ResultSetProperties(s));
		conversionService.register(SystemConversionService.getInstance());
		conversionService.register(new SqlDateConversionService());
		getMapper().setConverter(conversionService);
	}
}
