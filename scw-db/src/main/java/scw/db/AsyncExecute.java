package scw.db;

import java.io.Serializable;

/**
 * 推荐使用克隆
 * 
 * @author shuchaowen
 *
 */
public interface AsyncExecute extends Serializable {
	void execute(DB db);
}
