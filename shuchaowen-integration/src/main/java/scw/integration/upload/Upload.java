package scw.integration.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Upload {
	void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
