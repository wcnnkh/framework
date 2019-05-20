package scw.servlet.upload;

import scw.servlet.http.HttpRequest;
import scw.servlet.http.HttpResponse;

public interface Upload {
	void execute(HttpRequest request, HttpResponse response) throws Exception;
}
