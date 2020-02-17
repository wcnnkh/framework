package scw.mvc.logger;

import java.util.Collection;
import java.util.List;

import scw.beans.annotation.AutoImpl;
import scw.core.Pagination;

@AutoImpl(className = "scw.mvc.logger.db.DBLogServiceImpl")
public interface LogService {
	Pagination<List<Log>> getPagination(LogQuery logQuery, long page, int limit);

	Collection<String> getAttributeNames();

	void addLog(Log log);
}
