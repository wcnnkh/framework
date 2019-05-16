package scw.sql.orm;

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

import scw.core.FieldInfo;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.sql.orm.annotation.AutoCreate;
import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Counter;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.PrimaryKey;

public final class ColumnInfo {
	private static Logger logger = LoggerFactory.getLogger(ColumnInfo.class);

	private String name;// 数据库字段名
	private final boolean isPrimaryKey;// 索引
	private String typeName;
	private int length;
	private final boolean nullAble;// 是否可以为空
	private boolean unique;// 是否建立唯一索引
	private final boolean isDataBaseType;
	private final FieldInfo fieldInfo;
	private String sqlTableAndColumn;
	private final Column column;
	private final Counter counter;
	private final AutoIncrement autoIncrement;
	private final AutoCreate autoCreate;

	// 就是在name的两边加入了(``)
	private String sqlColumnName;

	protected ColumnInfo(String defaultTableName, FieldInfo field) {
		this.counter = field.getAnnotation(Counter.class);
		this.autoIncrement = field.getAnnotation(AutoIncrement.class);
		this.autoCreate = field.getAnnotation(AutoCreate.class);
		this.fieldInfo = field;
		this.name = field.getName();
		PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
		this.isPrimaryKey = primaryKey != null;
		Class<?> type = field.getType();
		this.typeName = type.getName();
		this.length = -1;
		this.isDataBaseType = ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type)
				|| URL.class.isAssignableFrom(type) || byte[].class.isAssignableFrom(type);
		this.column = field.getAnnotation(Column.class);
		if (column != null) {
			if (column.name().trim().length() != 0) {
				this.name = column.name().trim();
			}

			if (column.type().trim().length() != 0) {
				this.typeName = column.type().trim();
			}

			this.length = column.length();

			if (column.unique() || primaryKey != null || field.getAnnotation(Index.class) != null) {
				this.nullAble = false;
				if (column.nullAble()) {
					logger.warn("字段{}不能或不推荐设置为允许为空，因为他可能是主键或索引", getName());
				}
			} else {
				this.nullAble = column.nullAble();
			}
		} else {
			if (primaryKey != null || field.getAnnotation(Index.class) != null) {
				this.nullAble = false;
			} else {
				this.nullAble = !field.getType().isPrimitive();
			}
		}

		this.sqlColumnName = "`" + name + "`";
		this.sqlTableAndColumn = "`" + defaultTableName + "`." + sqlColumnName;
	}

	private Object fieldValueToDBValue(Object value) {
		if (boolean.class == fieldInfo.getType()) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == fieldInfo.getType()) {
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

	public void setValueToField(Object bean, Object dbValue) throws IllegalArgumentException, IllegalAccessException {
		fieldInfo.forceSet(bean, ORMUtils.parse(fieldInfo.getType(), dbValue));
	}

	public String getName() {
		return name;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public String getTypeName() {
		return typeName;
	}

	public Class<?> getType() {
		return fieldInfo.getType();
	}

	public int getLength() {
		return length;
	}

	public boolean isNullAble() {
		return nullAble;
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

	public boolean isUnique() {
		return unique;
	}

	public Column getColumn() {
		return column;
	}

	public Counter getCounter() {
		return counter;
	}

	public AutoIncrement getAutoIncrement() {
		return autoIncrement;
	}

	public AutoCreate getAutoCreate() {
		return autoCreate;
	}
}
