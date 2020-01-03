package scw.db;

import java.io.Serializable;

/**
 * 
 * @author shuchaowen
 *
 */
public abstract class BaseBean implements Serializable {
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
