package scw.mvc.action.logger;

import java.util.Collection;
import java.util.List;

import scw.beans.annotation.AutoImpl;
import scw.core.Pagination;

@AutoImpl(className = "scw.mvc.action.logger.db.DBActionLogServiceImpl")
public interface ActionLogService {
	Pagination<List<ActionLog>> getPagination(LogQuery logQuery, long page, int limit);

	Collection<String> getAttributeNames();

	void addLog(ActionLog log);
}
