package shuchaowen.core.db.storage;

import java.util.Collection;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.sql.SQL;

public interface StorageFactory {
	void execute(ConnectionOrigin connectionOrigin, Collection<SQL> sqls);
}
