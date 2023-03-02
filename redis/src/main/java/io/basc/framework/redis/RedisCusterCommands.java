package io.basc.framework.redis;

import java.util.List;

public interface RedisCusterCommands {
	byte[] cluster_addslots(int... slots);

	byte[] cluster_bumpepoch();

	Integer cluster_count_failure_reports(byte[] nodeId);

	Integer cluster_countkeysinslot(int slot);

	byte[] cluster_delslots(int... slots);

	byte[] cluster_failover(FailoverOption option);

	byte[] cluster_flushslots();

	byte[] cluster_forget(byte[] nodeId);

	List<byte[]> cluster_getkeysinslot(int slot, int count);

	byte[] cluster_info();

	Integer cluster_keyslot(byte[] key);

	byte[] cluster_meet(byte[] ip, int port);

	byte[] cluster_myid();

	List<byte[]> cluster_nodes();

	List<byte[]> cluster_replicas(byte[] nodeId);

	byte[] cluster_replicate(byte[] nodeId);

	byte[] cluster_reset(ResetType type);

	byte[] cluster_saveconfig();

	byte[] cluster_set_config_epoch();

	byte[] cluster_setslot(int slot, SetSlotOption option, byte[] nodeId);

	List<byte[]> cluster_slaves(byte[] nodeId);

	List<byte[]> cluster_slots();

	byte[] readonly();

	byte[] readwrite();
}
