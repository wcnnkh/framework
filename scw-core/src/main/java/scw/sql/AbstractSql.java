package scw.sql;

import scw.core.utils.ObjectUtils;

public abstract class AbstractSql implements Sql{
	
	public boolean isStoredProcedure() {
		return false;
	}
	
	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getSql(), getParams());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(obj == this){
			return true;
		}
		
		if(obj instanceof Sql){
			return ObjectUtils.nullSafeEquals(getSql(), ((Sql) obj).getSql()) && ObjectUtils.nullSafeEquals(getParams(), ((Sql) obj).getParams());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return SqlUtils.toString(getSql(), getParams());
	}
}
