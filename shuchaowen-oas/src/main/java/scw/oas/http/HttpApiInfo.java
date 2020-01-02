package scw.oas.http;

import java.util.List;

import scw.oas.ApiInfo;

public interface HttpApiInfo extends ApiInfo {
	String[] getMethods();

	String getPath();

	List<? extends HttpApiInfo> getSubApiInfoList();
}
