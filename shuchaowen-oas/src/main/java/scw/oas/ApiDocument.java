package scw.oas;

import java.util.List;

public interface ApiDocument extends ApiDescription {
	List<? extends ApiInfo> getApiInfoList();
}
