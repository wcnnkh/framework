package scw.tcc;

import scw.beans.annotation.AutoImpl;
import scw.tcc.service.MQTccService;
import scw.tcc.service.RetryTCCService;

@AutoImpl({ MQTccService.class, RetryTCCService.class })
public interface TCCService {

	void service(InvokeInfo invokeInfo);
}
