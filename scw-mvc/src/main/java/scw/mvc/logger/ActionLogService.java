package scw.mvc.logger;

import java.util.Collection;

import scw.util.Pagination;

public interface ActionLogService {
	Pagination<ActionLog> getPagination(ActionLog logQuery, long page, int limit);

	Collection<String> getAttributeNames();

	void addLog(ActionLog log);
}
