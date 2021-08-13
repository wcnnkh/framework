package scw.orm.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.lang.Nullable;

/**
 * 表结构
 * @author shuchaowen
 *
 */
public interface TableColumnDescriptor extends Iterable<Column> {
	/**
	 * 表名
	 * @return
	 */
	String getName();
	
	@Nullable
	default Column getColumn(String name) {
		for(Column column : this) {
			if(column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
	
	/**
	 * 对索引进行分组
	 * @return
	 */
	default Map<String, List<Column>> getIndexGroup(){
		Map<String, List<Column>> indexMap = new LinkedHashMap<String, List<Column>>();
		for(Column column : this){
			String indexName = column.getIndexName();
			if(StringUtils.isEmpty(indexName) && column.isUnique()){
				indexName = column.getName();
			}
			
			if(StringUtils.isEmpty(indexName)){
				continue;
			}
			
			List<Column> columns = indexMap.get(indexName);
			if(columns == null){
				columns = new ArrayList<Column>();
			}
			columns.add(column);
		}
		return indexMap;
	}
}
