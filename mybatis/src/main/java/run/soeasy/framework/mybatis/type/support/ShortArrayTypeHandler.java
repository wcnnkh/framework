package run.soeasy.framework.mybatis.type.support;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import run.soeasy.framework.mybatis.type.JsonTypeHandler;

@MappedTypes(short[].class)
@MappedJdbcTypes({ JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.NVARCHAR, JdbcType.LONGNVARCHAR })
public class ShortArrayTypeHandler extends JsonTypeHandler<short[]> {
}
