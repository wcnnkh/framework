package scw.database;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;

import scw.common.FieldInfo;
import scw.common.utils.ClassUtils;
import scw.database.annoation.Column;
import scw.database.annoation.PrimaryKey;

public final class ColumnInfo {
	private String name;// 数据库字段名
	private PrimaryKey primaryKey;// 索引
	private String typeName;
	private Class<?> type;
	private int length;
	private boolean nullAble;// 是否可以为空
	private boolean isDataBaseType;
	private FieldInfo fieldInfo;
	private Column column;
	private String sqlTableAndColumn;
	// 就是在name的两边加入了(``)
	private String sqlColumnName;

	protected ColumnInfo(String defaultTableName, FieldInfo field) {
		this.fieldInfo = field;
		this.name = field.getName();
		this.primaryKey = field.getField().getAnnotation(PrimaryKey.class);
		this.type = field.getType();
		this.typeName = this.type.getName();
		this.length = -1;
		this.nullAble = false;
		this.isDataBaseType = ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type)
				|| URL.class.isAssignableFrom(type) || byte[].class.isAssignableFrom(type);

		this.column = field.getField().getAnnotation(Column.class);
		if (column != null) {
			if (column.name().trim().length() != 0) {
				this.name = column.name().trim();
			}

			if (column.type().trim().length() != 0) {
				this.typeName = column.type().trim();
			}

			this.length = column.length();

			this.nullAble = column.nullAble();
		}

		this.sqlColumnName = "`" + name + "`";
		this.sqlTableAndColumn = "`" + defaultTableName + "`." + sqlColumnName;
	}

	public Object fieldValueToDBValue(Object value) {
		if (boolean.class == type) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == type) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}
		return value;
	}

	public Object getValueToDB(Object bean) throws IllegalArgumentException, IllegalAccessException {
		return fieldValueToDBValue(fieldInfo.forceGet(bean));
	}

	public Object dbValueToFieldValue(Object value) {
		if (boolean.class == type || Boolean.class == type) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).doubleValue() == 1;
				}
			}
		}
		return value;
	}

	public void setValueToField(Object bean, Object dbValue) throws IllegalArgumentException, IllegalAccessException {
		fieldInfo.forceSet(bean, dbValueToFieldValue(dbValue));
	}

	public String getName() {
		return name;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public String getTypeName() {
		return typeName;
	}

	public Class<?> getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public boolean isNullAble() {
		return nullAble;
	}

	public Column getColumn() {
		return column;
	}

	public String getSqlTableAndColumn() {
		return sqlTableAndColumn;
	}

	/**
	 * 就是在字段名的两边加上了(`)符号
	 * 
	 * @return
	 */
	public String getSqlColumnName() {
		return sqlColumnName;
	}

	/**
	 * 把指定的表名和字段组合在一起
	 * 
	 * @param tableName
	 * @return
	 */
	public String getSQLName(String tableName) {
		StringBuilder sb = new StringBuilder(32);
		if (tableName != null && tableName.length() != 0) {
			sb.append("`");
			sb.append(tableName);
			sb.append("`.");
		}
		sb.append(sqlColumnName);
		return sb.toString();
	}

	public boolean isDataBaseType() {
		return isDataBaseType;
	}

	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}
}
