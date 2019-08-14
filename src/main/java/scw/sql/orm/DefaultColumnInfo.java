package scw.sql.orm;

import java.lang.reflect.Field;

import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Counter;
import scw.sql.orm.enums.CasType;

public final class DefaultColumnInfo implements ColumnInfo {
	private final String name;// 数据库字段名
	private final Field field;
	private final Counter counter;
	private final CasType casType;
	private final ColumnConvert convert;

	protected DefaultColumnInfo(Field field) {
		this.counter = field.getAnnotation(Counter.class);
		this.field = field;
		this.name = ORMUtils.getAnnotationColumnName(field);
		this.casType = ORMUtils.getCasType(field);
		this.convert = ORMUtils.getConvert(field);
	}

	public String getName() {
		return name;
	}

	public boolean isPrimaryKey() {
		return ORMUtils.isAnnoataionPrimaryKey(field);
	}

	public String getTypeName() {
		return ORMUtils.getAnnotationColumnTypeName(field);
	}

	public Class<?> getType() {
		return field.getType();
	}

	public int getLength() {
		return ORMUtils.getAnnotationColumnLength(field);
	}

	public boolean isNullAble() {
		return ORMUtils.isAnnoataionColumnNullAble(field);
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
		sb.append("`").append(name).append("`");
		return sb.toString();
	}

	public boolean isDataBaseType() {
		return ORMUtils.isDataBaseType(field.getType());
	}

	public Field getField() {
		return field;
	}

	public boolean isUnique() {
		return ORMUtils.isAnnoataionColumnUnique(field);
	}

	public Counter getCounter() {
		return counter;
	}

	public boolean isAutoIncrement() {
		return field.getAnnotation(AutoIncrement.class) != null;
	}

	public CasType getCasType() {
		return casType;
	}

	public Object get(Object bean) throws Exception{
		return convert.getter(field, bean);
	}

	public void set(Object bean, Object value) throws Exception{
		convert.setter(field, bean, value);
	}
}
