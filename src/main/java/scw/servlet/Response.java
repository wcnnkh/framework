package scw.servlet;

import javax.servlet.ServletResponse;

public interface Response extends ServletResponse {
	void write(Object obj) throws Exception;
}
