package scw.db;

import java.io.Serializable;

public interface AsyncExecute extends Serializable {
	void execute(DB db);
}
