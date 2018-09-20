package shuchaowen.web.db.cache;

import java.io.Serializable;

public abstract class MemcachedCAS implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient long cas;
	
	public long getCas() {
		return cas;
	}
	
	public void setCas(long cas) {
		this.cas = cas;
	}
}
