package run.soeasy.framework.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.core.ResolvableType;

import lombok.NonNull;

public abstract class AbstractTypeHandler<S, T> extends BaseTypeHandler<S> {
	private Class<S> javaType;
	private Class<T> jdbcType;

	@SuppressWarnings("unchecked")
	public Class<S> getJavaType() {
		if (this.javaType == null) {
			this.javaType = (Class<S>) super.getRawType();
		}
		return this.javaType;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getJdbcType() {
		if (this.jdbcType == null) {
			this.jdbcType = (Class<T>) ResolvableType.forClass(getClass()).as(ConvertibleTypeHandler.class)
					.getGeneric(1).getRawClass();
		}
		return this.jdbcType;
	}

	@Override
	public S getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		T value = cs.getObject(columnIndex, getJdbcType());
		if (value == null) {
			return null;
		}
		return toJavaValue(getJavaType(), value);
	}

	@Override
	public S getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		T value = rs.getObject(columnIndex, getJdbcType());
		if (value == null) {
			return null;
		}
		return toJavaValue(getJavaType(), value);
	}

	@Override
	public S getNullableResult(ResultSet rs, String columnName) throws SQLException {
		T value = rs.getObject(columnName, getJdbcType());
		if (value == null) {
			return null;
		}
		return toJavaValue(getJavaType(), value);
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, S parameter, JdbcType jdbcType) throws SQLException {
		T value = toJdbcValue(getJdbcType(), parameter);
		ps.setObject(i, value);
	}

	public abstract S toJavaValue(Class<S> valueType, @NonNull T column);

	public abstract T toJdbcValue(Class<T> valueType, @NonNull S property);
}