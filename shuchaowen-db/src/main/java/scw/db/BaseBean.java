package scw.db;

import java.io.Serializable;

/**
 * 
 * @author shuchaowen
 *
 */
public abstract class BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public void save(){
		DBManager.getDB(this.getClass()).save(this);
	}
	
	public void update(){
		DBManager.getDB(this.getClass()).update(this);
	}
	
	public void delete(){
		DBManager.getDB(this.getClass()).delete(this);
	}
	
	public void saveOrUpdate(){
		DBManager.getDB(this.getClass()).saveOrUpdate(this);
	}
}
