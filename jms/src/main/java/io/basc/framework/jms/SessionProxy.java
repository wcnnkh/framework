package io.basc.framework.jms;

import javax.jms.Session;

public interface SessionProxy extends Session {

	Session getTargetSession();
}
