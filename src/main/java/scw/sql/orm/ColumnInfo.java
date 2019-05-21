package scw.sql.orm;

import java.lang.reflect.Field;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.sql.orm.annotation.AutoCreate;
import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Counter;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.PrimaryKey;

public final class ColumnInfo {
	private static Logger logger = LoggerFactory.getLogger(ColumnInfo.class);

	private String name;// 数据库字段名
	private final boolean primaryKey;// 索引
	private final boolean autoIncrement;
	private String typeName;
	private int length;
	private final boolean nullAble;// 是否可以为空
	private boolean unique;// 是否建立唯一索引
	private final boolean isDataBaseType;
	private final Field field;
	private String sqlTableAndColumn;
	private final Counter counter;
	private final AutoCreate autoCreate;

	// 就是在name的两边加入了(``)
	private String sqlColumnName;

	protected ColumnInfo(String defaultTableName, Field field) {
		this.counter = field.getAnnotation(Counter.class);
		this.autoIncrement = field.getAnnotation(AutoIncrement.class) != null;
		this.autoCreate = field.getAnnotation(AutoCreate.class);
		this.field = field;
		this.name = field.getName();
		PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
		this.primaryKey = primaryKey != null;
		Class<?> type = field.getType();
		this.typeName = type.getName();
		this.length = -1;
		this.isDataBaseType = ORMUtils.isDataBaseType(type);
		Column column = field.getAnnotation(Column.class);
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
		if (boolean.class == field.getType()) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == field.getType()) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}
		return value;
	}

	public Object getValueToDB(Object bean) throws IllegalArgumentException, IllegalAccessException {
		return fieldValueToDBValue(field.get(bean));
	}

	public void setValueToField(Object bean, Object dbValue) throws IllegalArgumentException, IllegalAccessException {
		field.set(bean, ORMUtils.parse(field.getType(), dbValue));
	}

	public String getName() {
		return name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public String getTypeName() {
		return typeName;
	}

	public Class<?> getType() {
		return field.getType();
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

	public Field getField() {
		return field;
	}

	public boolean isUnique() {
		return unique;
	}

	public Counter getCounter() {
		return counter;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public AutoCreate getAutoCreate() {
		return autoCreate;
	}
}
