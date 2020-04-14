package scw.mvc.action.logger;

import java.util.Collection;
import java.util.List;

import scw.core.Pagination;

public interface ActionLogService {
	Pagination<List<ActionLog>> getPagination(ActionLog logQuery, long page, int limit);

	Collection<String> getAttributeNames();

	void addLog(ActionLog log);
}
