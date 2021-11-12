package io.basc.framework.dubbo;

import java.util.List;

import org.apache.dubbo.config.ReferenceConfig;

public interface DubboReferenceConfigure {
	List<ReferenceConfig<?>> getReferenceConfigList();
}
