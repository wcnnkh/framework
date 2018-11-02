package shuchaowen.core.db.sql;

public class SimpleSQL implements SQL{
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;
	
	public SimpleSQL(){};
	
	public SimpleSQL(String sql, Object ...params){
		this.sql = sql;
		this.params = params;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
}
