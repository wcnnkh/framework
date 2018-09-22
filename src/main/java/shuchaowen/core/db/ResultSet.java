package shuchaowen.core.db;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultSet implements Serializable{
	private static final long serialVersionUID = -3199839587290797839L;
	private static final Result NULL_RESULT = new Result();
	private TableMapping tableMapping;
	private List<Result> dataList;
	
	public ResultSet(){};
	
	public ResultSet(java.sql.ResultSet resultSet) throws SQLException{
		render(resultSet);
	}
	
	public <T> T getFirst(Class<T> type){
		return get(type, 0);
	}
	
	public Result getFirst(){
		return get(0);
	}
	
	public <T> T getLast(Class<T> type){
		return getLast().get(type);
	}
	
	public Result getLast(){
		if(dataList == null || dataList.size() == 0){
			return NULL_RESULT;
		}
		
		return dataList.get(dataList.size() - 1);
	}
	
	public <T> T get(Class<T> type, int index){
		return get(index).get(type);
	}
	
	public Result get(int index){
		if(dataList == null || index < 0 || index >= dataList.size()){
			return NULL_RESULT;
		}
		
		return dataList.get(index);
	}
	
	public int size(){
		return dataList == null? 0:dataList.size();
	}
	
	public <T> List<T> getList(Class<T> type){
		if(dataList == null || dataList.isEmpty() || type == null){
			return null;
		}
		
		List<T> list = new ArrayList<T>(size());
		Iterator<Result> iterator = dataList.iterator();
		while(iterator.hasNext()){
			list.add(iterator.next().get(type));
		}
		return list;
	}
	
	public void render(java.sql.ResultSet resultSet) throws SQLException{
		if(resultSet == null){
			return;
		}
		
		if(dataList == null){
			dataList = new ArrayList<Result>();
		}else{
			dataList.clear();
		}
		
		append(resultSet);
	}
	
	public void append(java.sql.ResultSet resultSet) throws SQLException{
		if(resultSet == null){
			return;
		}
		
		if(dataList == null){
			dataList = new ArrayList<Result>();
		}
		
		while (resultSet.next()) {
			dataList.add(new Result(tableMapping, resultSet));
		}
	}
	
	public void registerClassTable(Class<?> tableClas, String tableName){
		if(tableMapping == null){
			tableMapping = new TableMapping();
		}
		tableMapping.register(tableClas, tableName);
	}
	
	public TableMapping getTableMapping() {
		return tableMapping;
	}

	public void setTableMapping(TableMapping tableMapping) {
		this.tableMapping = tableMapping;
	}

	public List<Result> getDataList() {
		return dataList;
	}

	public void setDataList(List<Result> dataList) {
		this.dataList = dataList;
	}
	
	public Iterator<Result> iterator(){
		return dataList==null? new ArrayList<Result>(0).iterator():dataList.iterator();
	}
}
