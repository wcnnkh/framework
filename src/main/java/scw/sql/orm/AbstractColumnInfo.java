package scw.sql.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.orm.sql.dialect.DefaultSqlType;
import scw.orm.sql.dialect.SqlType;
import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.enums.CasType;

public abstract class AbstractColumnInfo implements ColumnInfo {
	private final String name;// 数据库字段名
	private final Field field;
	private final CasType casType;
	private final AnnotationFactory annotationFactory;

	protected AbstractColumnInfo(Field field) {
		this.annotationFactory = new SimpleAnnotationFactory(field);
		this.field = field;
		this.name = ORMUtils.getAnnotationColumnName(field);
		this.casType = ORMUtils.getCasType(field);
	}
	
	public final String getName() {
		return name;
	}

	public final boolean isPrimaryKey() {
		return ORMUtils.isAnnoataionPrimaryKey(field);
	}

	public final boolean isNullAble() {
		return ORMUtils.isAnnoataionColumnNullAble(field);
	}

	/**
	 * 把指定的表名和字段组合在一起
	 * 
	 * @param tableName
	 * @return
	 */
	public final String getSQLName(String tableName) {
		StringBuilder sb = new StringBuilder(32);
		if (tableName != null && tableName.length() != 0) {
			sb.append("`");
			sb.append(tableName);
			sb.append("`.");
		}
		sb.append("`").append(name).append("`");
		return sb.toString();
	}

	public final Field getField() {
		return field;
	}

	public final SqlType getSqlType() {
		return new DefaultSqlType(ORMUtils.getAnnotationColumnTypeName(getField()),
				ORMUtils.getAnnotationColumnLength(getField()));
	}

	public final boolean isUnique() {
		return ORMUtils.isAnnoataionColumnUnique(field);
	}

	public final boolean isAutoIncrement() {
		return annotationFactory.getAnnotation(AutoIncrement.class) != null;
	}

	public final CasType getCasType() {
		return casType;
	}

	public final String getCharsetName() {
		return ORMUtils.getCharsetName(field);
	}

	public final <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}
}
