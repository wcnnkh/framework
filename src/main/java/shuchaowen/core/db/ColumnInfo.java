package shuchaowen.core.db;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.db.annoation.Column;
import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;

public final class ColumnInfo {
	private static final Map<String, ColumnFormat> COLUMN_FORMAT_MAP = new HashMap<String, ColumnFormat>();
	
	private String name;//数据库字段名
	private PrimaryKey primaryKey;//索引
	private String typeName;
	private Class<?> type;
	private int length;
	private boolean nullAble;//是否可以为空
	private boolean isDataBaseType;
	private FieldInfo fieldInfo;
	private Column column;
	private String sqlTableAndColumn;
	//就是在name的两边加入了(``)
	private String sqlColumnName;
	private ColumnFormat columnFormat = getColumnFormat(DefaultColumnFormat.class);
	
	public ColumnInfo(String defaultTableName, FieldInfo field){
		this.fieldInfo = field;
		this.name = field.getName();
		this.primaryKey = field.getField().getAnnotation(PrimaryKey.class);
		this.type = field.getType();
		this.typeName = this.type.getName();
		this.length = -1;
		this.nullAble = false;
		this.isDataBaseType = ClassUtils.isBasicType(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type)
				|| InputStream.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type)
				|| URL.class.isAssignableFrom(type)
				|| byte[].class.isAssignableFrom(type);
		
		this.column = field.getField().getAnnotation(Column.class);
		if(column != null){
			if(column.name().trim().length() != 0){
				this.name = column.name().trim();
			}
			
			if(column.type().trim().length() != 0){
				this.typeName = column.type().trim();
			}
			
			this.length = column.length();
			
			this.nullAble = column.nullAble();
			this.columnFormat = getColumnFormat(column.format());
		}
		
		this.sqlColumnName = "`" + name + "`";
		this.sqlTableAndColumn = "`" + defaultTableName + "`." + sqlColumnName;
	}
	
	public Object getValue(Object obj){
		try {
			return columnFormat.get(obj, this);
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public void setValue(Object obj, Object value){
		try {
			columnFormat.set(obj, this, value);
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}
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

	public ColumnFormat getColumnFormat() {
		return columnFormat;
	}

	/**
	 * 就是在字段名的两边加上了(`)符号
	 * @return
	 */
	public String getSqlColumnName() {
		return sqlColumnName;
	}

	/**
	 * 把指定的表名和字段组合在一起
	 * @param tableName
	 * @return
	 */
	public String getSQLName(String tableName){
		StringBuilder sb = new StringBuilder(32);
		if(tableName != null && tableName.length() != 0){
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
	
	private static ColumnFormat getColumnFormat(Class<? extends ColumnFormat> format){
		ColumnFormat columnFormat = COLUMN_FORMAT_MAP.get(format.getName());
		if(columnFormat == null){
			synchronized (COLUMN_FORMAT_MAP) {
				columnFormat = COLUMN_FORMAT_MAP.get(format.getName());
				if(columnFormat == null){
					try {
						if(format.getName().equals(ColumnFormat.class.getName())){
							columnFormat = new DefaultColumnFormat();
						}else{
							columnFormat = ClassUtils.newInstance(format);
						}
						COLUMN_FORMAT_MAP.put(format.getName(), columnFormat);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return columnFormat;
	}
}
