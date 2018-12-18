package scw.db.sql.mysql;

import java.util.Collection;
import java.util.Iterator;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.db.PrimaryKeyParameter;
import scw.db.TableInfo;
import scw.db.sql.SQL;

public class SelectINId implements SQL{
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;
	
	public SelectINId(TableInfo tableInfo, String tableName, Collection<PrimaryKeyParameter> primaryKeyParameters){
		if(primaryKeyParameters == null || primaryKeyParameters.isEmpty()){
			throw new ShuChaoWenRuntimeException("parameters length 0");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select * from `");
		sb.append(tableName);
		sb.append("` where ");
		
		this.params = new Object[primaryKeyParameters.size() * tableInfo.getPrimaryKeyColumns().length];
		int index = 0;
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			if(i != 0){
				sb.append(" and ");
			}
			
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getSQLName(tableName));
			sb.append(" in (");
			Iterator<PrimaryKeyParameter> iterator = primaryKeyParameters.iterator();
			while(iterator.hasNext()){
				PrimaryKeyParameter primaryKeyParameter = iterator.next();
				sb.append("?");
				params[index++] = primaryKeyParameter.getParams()[i];
				if(iterator.hasNext()){
					sb.append(",");
				}
			}
			sb.append(")");
		}
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
