package shuchaowen.core.db;

import java.io.Serializable;

/**
 * 此类仅在单服务器下使用
 * @author shuchaowen
 *
 */
public abstract class BaseBean implements Cloneable,Serializable {
	private static final long serialVersionUID = 1L;

	public final void save(){
		DBManager.getDB(this.getClass()).save(this);
	}
	
	public final void update(){
		DBManager.getDB(this.getClass()).update(this);
	}
	
	public final void delete(){
		DBManager.getDB(this.getClass()).delete(this);
	}
	
	public final void saveOrUpdate(){
		DBManager.getDB(this.getClass()).saveOrUpdate(this);
	}
}
