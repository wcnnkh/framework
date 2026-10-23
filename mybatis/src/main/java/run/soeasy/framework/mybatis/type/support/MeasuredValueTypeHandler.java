package run.soeasy.framework.mybatis.type.support;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import run.soeasy.framework.commons.unit.MeasuredValue;
import run.soeasy.framework.mybatis.type.JsonTypeHandler;

@MappedTypes(MeasuredValue.class)
@MappedJdbcTypes({ JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.NVARCHAR, JdbcType.LONGNVARCHAR })
public class MeasuredValueTypeHandler extends JsonTypeHandler<MeasuredValue> {
}
