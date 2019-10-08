package scw.beans.tcc;

import scw.beans.annotation.AutoImpl;
import scw.beans.tcc.service.MQTccService;
import scw.beans.tcc.service.RetryTCCService;

@AutoImpl({ MQTccService.class, RetryTCCService.class })
public interface TCCService {

	void service(InvokeInfo invokeInfo);
}
