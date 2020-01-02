package scw.oas.http;

import java.util.List;

import scw.oas.ApiDocument;

public interface HttpApiDocument extends ApiDocument {
	List<? extends HttpApiInfo> getApiInfoList();
}
