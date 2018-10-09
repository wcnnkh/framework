package shuchaowen.core.db;

import java.util.Collection;

public final class PrimaryKeyParameter{
	private final Object[] params;
	
	public PrimaryKeyParameter(Collection<Object> params) {
		this.params = params.toArray();
	}
	
	public PrimaryKeyParameter(Object ...params) {
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PrimaryKeyParameter){
			PrimaryKeyParameter primaryKeyParameter = (PrimaryKeyParameter) obj;
			if(primaryKeyParameter.getParams().length != getParams().length){
				return false;
			}
			
			if(getParams().length == 0){
				return true;
			}
			
			for(int i=0; i<getParams().length; i++){
				//主键不可能为空
				if(!getParams()[i].equals(primaryKeyParameter.getParams()[i])){
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
