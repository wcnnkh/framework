package run.soeasy.framework.jdbc.convert;

import java.sql.ResultSet;

import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;

public class ResultSetMapper extends BeanMapper {
	private ConversionService conversionService = new ConversionService();
	
	public ResultSetMapper() {
		getMappingProvider().registerFactory(ResultSet.class, (s, e) -> new ResultSetProperties(s));
		conversionService.register(SystemConversionService.getInstance());
		conversionService.register(new SqlDateConversionService());
		getMapper().setConverter(conversionService);
	}
}
