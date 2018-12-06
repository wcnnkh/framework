package shuchaowen.core.db.sql.format;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.utils.Pagination;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TableMapping;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public abstract class Select{
	private Map<String, String> associationWhereMap;
	private TableMapping tableMapping;
	private HashSet<String> selectTableSet;
	protected AbstractDB db;
	
	public Select(AbstractDB db) {
		this.db = db;
	}
	
	public Select from(Class<?> tableClass){
		if(selectTableSet == null){
			selectTableSet = new HashSet<String>();
		}
		
		selectTableSet.add(getTableName(tableClass));
		return this;
	}
	
	protected Map<String, String> getAssociationWhereMap(){
		return associationWhereMap;
	}
	
	public String getAssociationWhere(){
		if(associationWhereMap == null || associationWhereMap.isEmpty()){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> iterator = associationWhereMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			
			if(iterator.hasNext()){
				sb.append(" and ");
			}
		}
		return sb.toString();
	}
	
	public Select registerTableName(Class<?> tableClass, String tableName){
		if(tableMapping == null){
			tableMapping = new TableMapping();
		}
		
		tableMapping.register(tableClass, tableName);
		return this;
	}
	
	public String getSQLColumn(Class<?> tableClass, String name){
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		return tableInfo.getColumnInfo(name).getSQLName(getTableName(tableClass));
	}
	
	public String getTableName(Class<?> tableClass){
		String name = null;
		if(tableMapping != null){
			name = tableMapping.getTableName(tableClass);
		}
		if(name == null){
			TableInfo tableInfo = DB.getTableInfo(tableClass);
			if(!tableInfo.isTable()){
				throw new ShuChaoWenRuntimeException(tableClass.getName() + " not found @Table");
			}
			
			name = tableInfo.getName();
		}
		return name;
	}
	
	public TableMapping getTableMapping() {
		return tableMapping;
	}

	public void setTableMapping(TableMapping tableMapping) {
		this.tableMapping = tableMapping;
	}

	protected void addSelectTable(String tableName){
		if(selectTableSet == null){
			selectTableSet = new HashSet<String>();
		}
		selectTableSet.add(tableName);
	}
	
	public String getSelectTables(){
		if(selectTableSet == null || selectTableSet.isEmpty()){
			throw new NullPointerException("select tables");
		}
		
		StringBuilder sb = new StringBuilder();
		Iterator<String> iterator = selectTableSet.iterator();
		while(iterator.hasNext()){
			sb.append("`");
			sb.append(iterator.next());
			sb.append("`");
			
			if(iterator.hasNext()){
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public HashSet<String> getSelectTableSet() {
		return selectTableSet;
	}

	/**
	 * 检查两个字段在map是否存在相互引用的情况
	 * 
	 * @param whereMap
	 * @param name1
	 * @param name2
	 */
	private static boolean checkWhere(Map<String, String> whereMap,
			String name1, String name2) {
		if (whereMap.containsKey(name1)) {
			String v = whereMap.get(name1);
			if (name2.equals(v)) {
				return true;
			} else {
				return checkWhere(whereMap, v, name2);
			}
		} else if (whereMap.containsKey(name2)) {
			String v = whereMap.get(name2);
			if (name1.equals(v)) {
				return true;
			} else {
				return checkWhere(whereMap, v, name1);
			}
		}
		return false;
	}

	public AbstractDB getDb() {
		return db;
	}
	
	public abstract SQL toSQL(String select, boolean order);
	
	/**
	 * 把table2的指定字段和table1的主键关联
	 * @param tableClass1
	 * @param tableClass2
	 * @param table2Columns 如果不填写就是两个表的主键关联
	 * @return
	 */
	public Select associationQuery(Class<?> tableClass1, Class<?> tableClass2, String ...table2Columns){
		if(associationWhereMap == null){
			associationWhereMap = new HashMap<String, String>();
		}
		
		TableInfo t1 = DB.getTableInfo(tableClass1);
		TableInfo t2 = DB.getTableInfo(tableClass2);
		String tName1 = getTableName(tableClass1);
		String tName2 = getTableName(tableClass2);
		if(table2Columns == null || table2Columns.length == 0){
			if(t1.getPrimaryKeyColumns().length != t2.getPrimaryKeyColumns().length){
				//两张表的主键数量不一致
				throw new ShuChaoWenRuntimeException("primary key count atypism");
			}
			
			for(int i=0; i<t1.getPrimaryKeyColumns().length; i++){
				String n1 = t1.getPrimaryKeyColumns()[i].getSQLName(tName1);
				String n2 = t2.getPrimaryKeyColumns()[i].getSQLName(tName2);
				if(checkWhere(associationWhereMap, n1, n2)){
					continue;
				}
				
				associationWhereMap.put(n1, n2);
			}
		}else{
			if(table2Columns.length != t1.getPrimaryKeyColumns().length){
				//指明的外键和主键数量不一致
				throw new ShuChaoWenRuntimeException("primary key count atypism");
			}
			
			for(int i=0; i<table2Columns.length; i++){
				String n1 = t2.getColumnInfo(table2Columns[i]).getSQLName(tName2);
				String n2 = t1.getPrimaryKeyColumns()[i].getSQLName(tName1);
				if(checkWhere(associationWhereMap, n1, n2)){
					continue;
				}
				associationWhereMap.put(n1, n2);
			}
		}
		
		if(selectTableSet == null){
			selectTableSet = new HashSet<String>();
		}
		
		selectTableSet.add(tName1);
		selectTableSet.add(tName2);
		return this;
	}
	
	public abstract Select whereAnd(String where, Collection<?> values);
	
	public Select whereAnd(String where, Object ...value){
		return whereAnd(where, Arrays.asList(value));
	}
	
	public abstract Select whereOr(String where, Collection<?> values);
	
	public Select whereOr(String where, Object ...value){
		return whereOr(where, Arrays.asList(value));
	}

	public abstract Select whereAndValue(Class<?> tableClass, String name, Object value);
	
	public abstract Select whereOrValue(Class<?> tableClass, String name, Object value);
	
	public Select whereAndIn(Class<?> tableClass, String name, Object ...value){
		return whereAndIn(tableClass, name, Arrays.asList(value));
	}

	public abstract Select whereAndIn(Class<?> tableClass, String name, Collection<?> values);

	public abstract Select whereOrIn(Class<?> tableClass, String name, Collection<?> values);

	public Select whereOrIn(Class<?> tableClass, String name, Object ...value){
		return whereOrIn(tableClass, name, Arrays.asList(value	));
	}
	
	/**
	 * 降序
	 * @param tableClass
	 * @param nameList
	 * @return
	 */
	public abstract Select desc(Class<?> tableClass, Collection<String> nameList);
	
	public Select desc(Class<?> tableClass, String ...names){
		return desc(tableClass, Arrays.asList(names));
	}

	/**
	 * 升序
	 * @param tableClass
	 * @param nameList
	 * @return
	 */
	public abstract Select asc(Class<?> tableClass, Collection<String> nameList);
	
	public Select asc(Class<?> tableClass, String ...names){
		return asc(tableClass, Arrays.asList(names));
	}

	public abstract long count();
	
	public <T> T getFirst(Class<T> type){
		return getFirstResult().get(type);
	}
	
	public Result getFirstResult(){
		return getResultSet(0, 1).getFirst();
	}
	
	public abstract ResultSet getResultSet();
	
	public <T> List<T> getList(Class<T> type){
		return getResultSet().getList(type);
	}
	
	public abstract ResultSet getResultSet(long begin, int limit);
	
	public <T> List<T> getList(Class<T> type, long begin, int limit){
		return getResultSet(begin, limit).getList(type);
	}
	
	public Pagination<ResultSet> getResultSetPagination(long page, int limit){
		Pagination<ResultSet> pagination = new Pagination<ResultSet>();
		pagination.setLimit(limit);
		if(page <= 0 || limit <= 0){
			return pagination;
		}
		
		long count = count();
		if(count == 0){
			return pagination;
		}
		
		pagination.setTotalCount(count);
		pagination.setData(getResultSet((page - 1) * limit, limit));
		return pagination;
	}
	
	public <T> Pagination<List<T>> getPagination(Class<T> type, long page, int limit){
		Pagination<List<T>> pagination = new Pagination<List<T>>();
		pagination.setLimit(limit);
		if(page <= 0 || limit <= 0){
			return pagination;
		}
		
		long count = count();
		if(count == 0){
			return pagination;
		}
		
		pagination.setTotalCount(count);
		pagination.setData(getList(type, (page - 1) * limit, limit));
		return pagination;
	}
	
	public abstract void iterator(ResultIterator iterator);
	
	public abstract void iterator(long begin, long limit, ResultIterator iterator);
}
