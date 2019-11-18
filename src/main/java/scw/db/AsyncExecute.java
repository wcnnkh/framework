package scw.db;

import java.io.Serializable;

/**
 * 注意，在插入时推荐对此实例克隆
 * @author shuchaowen
 *
 */
public interface AsyncExecute extends Serializable {
	void execute(DB db);
}
