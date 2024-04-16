package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.util.select.Weighted;

/**
 * 负载均衡节点
 * 
 * @author wcnnkh
 *
 */
public interface Node extends Weighted {
	/**
	 * 服务的id,唯一值
	 * 
	 * @return
	 */
	String getId();

	/**
	 * 服务名称，可以重复
	 * 
	 * @return
	 */
	String getName();
}