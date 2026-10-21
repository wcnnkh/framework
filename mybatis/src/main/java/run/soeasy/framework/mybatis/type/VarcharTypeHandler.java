package run.soeasy.framework.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;

@MappedJdbcTypes({ JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.NVARCHAR, JdbcType.LONGNVARCHAR })
public class VarcharTypeHandler<S> extends ConvertibleTypeHandler<S, String> {

	public VarcharTypeHandler(@NonNull Converter converter) {
		super(converter);
	}

}
