package io.basc.framework.orm.convert;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.lang.ConvertiblePair;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

/**
 * 这是一个很简单的ResultSet映射
 * @author shuchaowen
 *
 */
public class ResultSetRowToEntityConversionService extends EntityConversionService {
	
	public ResultSetRowToEntityConversionService() {
		//使用严格模式
		setStrict(true);
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(ResultSet.class, Object.class));
	}

	@Override
	protected Enumeration<String> keys(Object source) {
		ResultSet resultSet = (ResultSet) source;
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			int count = metaData.getColumnCount();
			String[] names = new String[count];
			// result的索引是从1开始的
			for (int i = 1; i <= count; i++) {
				names[i - 1] = metaData.getColumnName(i);
			}
			return Collections.enumeration(Arrays.asList(names));
		} catch (SQLException e) {
			throw new ConversionException(String.valueOf(source), e);
		}
	}

	@Override
	protected Object getProperty(Object source, String key) {
		try {
			return ((ResultSet) source).getObject(key);
		} catch (SQLException e) {
			throw new ConversionException(key + "-" + String.valueOf(source), e);
		}
	}

}
