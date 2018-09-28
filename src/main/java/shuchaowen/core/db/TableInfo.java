package shuchaowen.core.db;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.annoation.NotColumn;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheFactory;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.exception.KeyAlreadyExistsException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public final class TableInfo {
	private String name;
	private Table table;
	private String engine = "InnoDB";
	private String charset = "utf8";
	private String row_format = "COMPACT";
	
	public static final String CAS_VERSION_COLUMN = "scw_cas_version";
	public static final String SQL_CAS_VERSION_COLUMN = "`" + CAS_VERSION_COLUMN + "`";
	private String tableAndCasColumn;
	
	private Map<String, ColumnInfo> columnMap = new HashMap<String, ColumnInfo>();//所有的  数据库字段名到字段的映射
	private Map<String, String> fieldToColumn = new HashMap<String, String>();//所有的  字段名数据库名的映射
	//字段的set方法，如果没有set方法就不到这个集合里面
	//用来做监听非主键字段的更新
	private Map<String, ColumnInfo> notPrimaryKeySetterNameMap = new HashMap<String, ColumnInfo>();
	
	private ColumnInfo[] columns;
	private ColumnInfo[] primaryKeyColumns;
	private ColumnInfo[] notPrimaryKeyColumns;
	private ColumnInfo[] tableColumns;
	private Class<?>[] proxyInterface;
	private Cache cache;
	
	public TableInfo(ClassInfo classInfo) {
		//动态代理实现的接口
		this.proxyInterface = getBeanProxyInterface(classInfo.getClz());
		
		StringBuilder sb = new StringBuilder();
		char[] chars = classInfo.getSimpleName().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c)) {// 如果是大写的
				if (i != 0) {
					sb.append("_");
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		
		this.name = sb.toString();
		
		boolean parent = false;
		this.table = classInfo.getClz().getAnnotation(Table.class);
		if(table != null){
			parent = table.parent();
			if(!"".equals(table.name())){
				this.name = table.name();
			}
			
			if(!StringUtils.isNull(table.engine())){
				this.engine = table.engine();
			}
			
			if(!StringUtils.isNull(table.charset())){
				this.charset = table.charset();
			}
			
			if(!StringUtils.isNull(table.row_format())){
				this.row_format = table.row_format();
			}
			
			this.cache = getCache(table.cacheFactory(), classInfo.getClz());
			
		}
		
		List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> idNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> notIdNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> tableColumnList = new ArrayList<ColumnInfo>();
		
		ClassInfo tempClassInfo = classInfo;
		while(tempClassInfo != null){
			for (String fieldName : tempClassInfo.getFieldNames()) {
				FieldInfo fieldInfo = tempClassInfo.getFieldMap().get(fieldName);
				NotColumn exclude = fieldInfo.getField().getAnnotation(NotColumn.class);
				if(exclude != null){
					continue;
				}
				
				if (Modifier.isStatic(fieldInfo.getField().getModifiers()) 
						|| Modifier.isFinal(fieldInfo.getField().getModifiers())) {
					continue;
				}
				
				ColumnInfo columnInfo = new ColumnInfo(name, fieldInfo);
				if(columnMap.containsKey(columnInfo.getName()) || fieldToColumn.containsKey(fieldInfo.getName())){
					throw new KeyAlreadyExistsException("[" + columnInfo.getName() + "]字段已存在");
				}
				
				this.tableAndCasColumn = "`" + name + "`." + SQL_CAS_VERSION_COLUMN;
				this.columnMap.put(columnInfo.getName(), columnInfo);
				this.fieldToColumn.put(fieldInfo.getName(), columnInfo.getName());
				
				
				
				if(columnInfo.isDataBaseType()){
					allColumnList.add(columnInfo);
					if (columnInfo.getPrimaryKey() != null) {
						idNameList.add(columnInfo);
					} else {
						notIdNameList.add(columnInfo);
						if(fieldInfo.getSetter() != null){
							this.notPrimaryKeySetterNameMap.put(fieldInfo.getSetter().getName(), columnInfo);
						}
					}
				}else{
					boolean javaType = fieldInfo.getField().getType().getName().startsWith("java.") || fieldInfo.getField().getType().getName().startsWith("javax.");
					if(!javaType){
						tableColumnList.add(columnInfo);
					}
				}
			}
			
			if(parent){
				tempClassInfo = tempClassInfo.getSuperInfo();
				if(tempClassInfo != null){
					Table table = tempClassInfo.getClz().getAnnotation(Table.class);
					if(table != null && !table.parent()){
						break;
					}
				}
			}else{
				break;
			}
		}
		
		this.columns = allColumnList.toArray(new ColumnInfo[allColumnList.size()]);
		this.primaryKeyColumns = idNameList.toArray(new ColumnInfo[0]);
		this.notPrimaryKeyColumns = notIdNameList.toArray(new ColumnInfo[0]);
		this.tableColumns = tableColumnList.toArray(new ColumnInfo[tableColumnList.size()]);
	}
	
	public String getName() {
		return name;
	}

	public String getEngine() {
		return engine;
	}

	public String getCharset() {
		return charset;
	}

	public String getRow_format() {
		return row_format;
	}
	
	public ColumnInfo getColumnInfo(String fieldName){
		ColumnInfo columnInfo = columnMap.get(fieldName);
		if(columnInfo == null){
			String v = fieldToColumn.get(fieldName);
			if(v == null){
				throw new NullPointerException("not found table["+ this.name+"] fieldName["+ fieldName +"]");
			}
			
			columnInfo = columnMap.get(v);
		}
		return columnInfo;
	}

	public Table getTable() {
		return table;
	}

	public Map<String, String> getFieldToColumn() {
		return fieldToColumn;
	}
	
	public ColumnInfo[] getColumns() {
		return columns;
	}

	public ColumnInfo[] getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public ColumnInfo[] getNotPrimaryKeyColumns() {
		return notPrimaryKeyColumns;
	}

	public ColumnInfo getColumnByNotPrimaryKeySetterNameMap(String setterMethodName) {
		return notPrimaryKeySetterNameMap.get(setterMethodName);
	}

	public String getTableAndCasColumn() {
		return tableAndCasColumn;
	}
	
	public String getTableAndCasColumn(String tableName) {
		if(tableName == null || tableName.length() == 0){
			return SQL_CAS_VERSION_COLUMN;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("`");
		sb.append(tableName);
		sb.append("`.");
		sb.append(SQL_CAS_VERSION_COLUMN);
		return sb.toString();
	}

	public Class<?>[] getProxyInterface() {
		return proxyInterface;
	}
	
	public static Class<?>[] getBeanProxyInterface(Class<?> type){
		Class<?>[] arr = type.getInterfaces();
		Class<?>[] newArr = new Class<?>[arr==null? 1:arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[newArr.length - 1] = BeanProxy.class;
		return newArr;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public static Cache getCache(Class<? extends CacheFactory> cacheFactoryClass, Class<?> tableClass){
		CacheFactory cacheFactory = DB.getCacheFactory(cacheFactoryClass);
		if(cacheFactory == null){
			return null;
		}
		return cacheFactory.getCache(tableClass);
	}
	
	public boolean isTable(){
		return table != null;
	}

	/*
	 * 这些字段都是实体类，并且对应着表
	 * @return
	 */
	public ColumnInfo[] getTableColumns() {
		return tableColumns;
	}
}
