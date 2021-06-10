package scw.mysql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;

import scw.core.utils.ClassUtils;
import scw.orm.sql.SqlType;
import scw.orm.sql.SqlTypeMapping;

public class MysqlTypeMapping implements SqlTypeMapping {

	@Override
	public SqlType getSqlType(Class<?> type) {
		if (ClassUtils.isString(type) || type.isEnum()) {
			return SqlTypes.VARCHAR;
		} else if (ClassUtils.isBoolean(type)) {
			return SqlTypes.BIT;
		} else if (ClassUtils.isByte(type)) {
			return SqlTypes.TINYINT;
		} else if (ClassUtils.isShort(type)) {
			return SqlTypes.SMALLINT;
		} else if (ClassUtils.isInt(type)) {
			return SqlTypes.INT;
		} else if (ClassUtils.isLong(type)) {
			return SqlTypes.BIGINT;
		} else if (ClassUtils.isFloat(type)) {
			return SqlTypes.FLOAT;
		} else if (ClassUtils.isDouble(type)) {
			return SqlTypes.DOUBLE;
		} else if (Date.class.isAssignableFrom(type)) {
			return SqlTypes.DATE;
		} else if (Timestamp.class.isAssignableFrom(type)) {
			return SqlTypes.TIMESTAMP;
		} else if (Time.class.isAssignableFrom(type)) {
			return SqlTypes.TIME;
		} else if (Year.class.isAssignableFrom(type)) {
			return SqlTypes.YEAR;
		} else if (Blob.class.isAssignableFrom(type)) {
			return SqlTypes.BLOB;
		} else if (Clob.class.isAssignableFrom(type)) {
			return SqlTypes.BLOB;
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return SqlTypes.DECIMAL;
		} else {
			return SqlTypes.TEXT;
		}
	}

}
