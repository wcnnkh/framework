package io.basc.framework.netflix.eureka.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.aws.AwsBindingStrategy;

import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.orm.annotation.ConfigurationProperties;

@ConfigurationProperties(EurekaServerConfigBean.PREFIX)
public class EurekaServerConfigBean implements EurekaServerConfig {

	/**
	 * Eureka server configuration properties prefix.
	 */
	public static final String PREFIX = "eureka.server";

	@Autowired(required = false)
	private Environment environment;

	private String aWSAccessId;

	private String aWSSecretKey;

	private int eIPBindRebindRetries = 3;

	private int eIPBindingRetryIntervalMs = (int) TimeUnit.MINUTES.toMillis(5);

	private int eIPBindingRetryIntervalMsWhenUnbound = (int) TimeUnit.MINUTES.toMillis(1);

	private boolean enableSelfPreservation = true;

	private double renewalPercentThreshold = 0.85;

	private int renewalThresholdUpdateIntervalMs = (int) TimeUnit.MINUTES.toMillis(15);

	private int peerEurekaNodesUpdateIntervalMs = (int) TimeUnit.MINUTES.toMillis(10);

	private int numberOfReplicationRetries = 5;

	private int peerEurekaStatusRefreshTimeIntervalMs = 30 * 1000;

	private int waitTimeInMsWhenSyncEmpty = (int) TimeUnit.MINUTES.toMillis(5);

	private int peerNodeConnectTimeoutMs = 200;

	private int peerNodeReadTimeoutMs = 200;

	private int peerNodeTotalConnections = 1000;

	private int peerNodeTotalConnectionsPerHost = 500;

	private int peerNodeConnectionIdleTimeoutSeconds = 30;

	private long retentionTimeInMSInDeltaQueue = TimeUnit.MINUTES.toMillis(3);

	private long deltaRetentionTimerIntervalInMs = 30 * 1000;

	private long evictionIntervalTimerInMs = 60 * 1000;

	private int aSGQueryTimeoutMs = 300;

	private long aSGUpdateIntervalMs = TimeUnit.MINUTES.toMillis(5);

	private long aSGCacheExpiryTimeoutMs = TimeUnit.MINUTES.toMillis(10); // defaults to longer
	// than the

	// asg update interval

	private long responseCacheAutoExpirationInSeconds = 180;

	private long responseCacheUpdateIntervalMs = 30 * 1000;

	private boolean useReadOnlyResponseCache = true;

	private boolean disableDelta;

	private long maxIdleThreadInMinutesAgeForStatusReplication = 10;

	private int minThreadsForStatusReplication = 1;

	private int maxThreadsForStatusReplication = 1;

	private int maxElementsInStatusReplicationPool = 10000;

	private boolean syncWhenTimestampDiffers = true;

	private int registrySyncRetries = 0;

	private long registrySyncRetryWaitMs = 30 * 1000;

	private int maxElementsInPeerReplicationPool = 10000;

	private long maxIdleThreadAgeInMinutesForPeerReplication = 15;

	private int minThreadsForPeerReplication = 5;

	private int maxThreadsForPeerReplication = 20;

	private int maxTimeForReplication = 30000;

	private boolean primeAwsReplicaConnections = true;

	private boolean disableDeltaForRemoteRegions;

	private int remoteRegionConnectTimeoutMs = 1000;

	private int remoteRegionReadTimeoutMs = 1000;

	private int remoteRegionTotalConnections = 1000;

	private int remoteRegionTotalConnectionsPerHost = 500;

	private int remoteRegionConnectionIdleTimeoutSeconds = 30;

	private boolean gZipContentFromRemoteRegion = true;

	private Map<String, String> remoteRegionUrlsWithName = new HashMap<>();

	private String[] remoteRegionUrls;

	private Map<String, Set<String>> remoteRegionAppWhitelist = new HashMap<>();

	private int remoteRegionRegistryFetchInterval = 30;

	private int remoteRegionFetchThreadPoolSize = 20;

	private String remoteRegionTrustStore = "";

	private String remoteRegionTrustStorePassword = "changeit";

	private boolean disableTransparentFallbackToOtherRegion;

	private boolean batchReplication;

	private boolean rateLimiterEnabled = false;

	private boolean rateLimiterThrottleStandardClients = false;

	private Set<String> rateLimiterPrivilegedClients = Collections.emptySet();

	private int rateLimiterBurstSize = 10;

	private int rateLimiterRegistryFetchAverageRate = 500;

	private int rateLimiterFullFetchAverageRate = 100;

	private boolean logIdentityHeaders = true;

	private String listAutoScalingGroupsRoleName = "ListAutoScalingGroups";

	private boolean enableReplicatedRequestCompression = false;

	private String jsonCodecName;

	private String xmlCodecName;

	private int route53BindRebindRetries = 3;

	private int route53BindingRetryIntervalMs = (int) TimeUnit.MINUTES.toMillis(5);

	private long route53DomainTTL = 30;

	private AwsBindingStrategy bindingStrategy = AwsBindingStrategy.EIP;

	private int minAvailableInstancesForPeerReplication = -1;

	private int initialCapacityOfResponseCache = 1000;

	private int expectedClientRenewalIntervalSeconds = 30;

	private boolean useAwsAsgApi = true;

	private String myUrl;

	@Override
	public boolean shouldEnableSelfPreservation() {
		return this.enableSelfPreservation;
	}

	@Override
	public boolean shouldDisableDelta() {
		return this.disableDelta;
	}

	@Override
	public boolean shouldSyncWhenTimestampDiffers() {
		return this.syncWhenTimestampDiffers;
	}

	@Override
	public boolean shouldPrimeAwsReplicaConnections() {
		return this.primeAwsReplicaConnections;
	}

	@Override
	public boolean shouldDisableDeltaForRemoteRegions() {
		return this.disableDeltaForRemoteRegions;
	}

	@Override
	public boolean shouldGZipContentFromRemoteRegion() {
		return this.gZipContentFromRemoteRegion;
	}

	@Override
	public Set<String> getRemoteRegionAppWhitelist(String regionName) {
		return this.remoteRegionAppWhitelist.get(regionName == null ? "global" : regionName.trim().toLowerCase());
	}

	@Override
	public boolean disableTransparentFallbackToOtherRegion() {
		return this.disableTransparentFallbackToOtherRegion;
	}

	@Override
	public boolean shouldBatchReplication() {
		return this.batchReplication;
	}

	@Override
	public String getMyUrl() {
		return this.myUrl;
	}

	public void setMyUrl(String myUrl) {
		this.myUrl = myUrl;
	}

	@Override
	public boolean shouldLogIdentityHeaders() {
		return this.logIdentityHeaders;
	}

	@Override
	public String getJsonCodecName() {
		return this.jsonCodecName;
	}

	@Override
	public String getXmlCodecName() {
		return this.xmlCodecName;
	}

	@Override
	public boolean shouldUseReadOnlyResponseCache() {
		return this.useReadOnlyResponseCache;
	}

	@Override
	public boolean shouldEnableReplicatedRequestCompression() {
		return this.enableReplicatedRequestCompression;
	}

	@Override
	public String getExperimental(String name) {
		if (this.environment != null) {
			return environment.getProperties().getAsString(PREFIX + ".experimental." + name);
		}
		return null;
	}

	@Override
	public int getInitialCapacityOfResponseCache() {
		return this.initialCapacityOfResponseCache;
	}

	public void setInitialCapacityOfResponseCache(int initialCapacityOfResponseCache) {
		this.initialCapacityOfResponseCache = initialCapacityOfResponseCache;
	}

	@Override
	public int getHealthStatusMinNumberOfAvailablePeers() {
		return this.minAvailableInstancesForPeerReplication;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String getAWSAccessId() {
		return aWSAccessId;
	}

	public void setAWSAccessId(String aWSAccessId) {
		this.aWSAccessId = aWSAccessId;
	}

	public String getAWSSecretKey() {
		return aWSSecretKey;
	}

	public void setAWSSecretKey(String aWSSecretKey) {
		this.aWSSecretKey = aWSSecretKey;
	}

	public int getEIPBindRebindRetries() {
		return eIPBindRebindRetries;
	}

	public void setEIPBindRebindRetries(int eIPBindRebindRetries) {
		this.eIPBindRebindRetries = eIPBindRebindRetries;
	}

	public int getEIPBindingRetryIntervalMs() {
		return eIPBindingRetryIntervalMs;
	}

	public void setEIPBindingRetryIntervalMs(int eIPBindingRetryIntervalMs) {
		this.eIPBindingRetryIntervalMs = eIPBindingRetryIntervalMs;
	}

	public int getEIPBindingRetryIntervalMsWhenUnbound() {
		return eIPBindingRetryIntervalMsWhenUnbound;
	}

	public void setEIPBindingRetryIntervalMsWhenUnbound(int eIPBindingRetryIntervalMsWhenUnbound) {
		this.eIPBindingRetryIntervalMsWhenUnbound = eIPBindingRetryIntervalMsWhenUnbound;
	}

	public boolean isEnableSelfPreservation() {
		return enableSelfPreservation;
	}

	public void setEnableSelfPreservation(boolean enableSelfPreservation) {
		this.enableSelfPreservation = enableSelfPreservation;
	}

	@Override
	public double getRenewalPercentThreshold() {
		return renewalPercentThreshold;
	}

	public void setRenewalPercentThreshold(double renewalPercentThreshold) {
		this.renewalPercentThreshold = renewalPercentThreshold;
	}

	@Override
	public int getRenewalThresholdUpdateIntervalMs() {
		return renewalThresholdUpdateIntervalMs;
	}

	@Override
	public int getExpectedClientRenewalIntervalSeconds() {
		return this.expectedClientRenewalIntervalSeconds;
	}

	public void setExpectedClientRenewalIntervalSeconds(int expectedClientRenewalIntervalSeconds) {
		this.expectedClientRenewalIntervalSeconds = expectedClientRenewalIntervalSeconds;
	}

	public void setRenewalThresholdUpdateIntervalMs(int renewalThresholdUpdateIntervalMs) {
		this.renewalThresholdUpdateIntervalMs = renewalThresholdUpdateIntervalMs;
	}

	@Override
	public int getPeerEurekaNodesUpdateIntervalMs() {
		return peerEurekaNodesUpdateIntervalMs;
	}

	public void setPeerEurekaNodesUpdateIntervalMs(int peerEurekaNodesUpdateIntervalMs) {
		this.peerEurekaNodesUpdateIntervalMs = peerEurekaNodesUpdateIntervalMs;
	}

	@Override
	public int getNumberOfReplicationRetries() {
		return numberOfReplicationRetries;
	}

	public void setNumberOfReplicationRetries(int numberOfReplicationRetries) {
		this.numberOfReplicationRetries = numberOfReplicationRetries;
	}

	@Override
	public int getPeerEurekaStatusRefreshTimeIntervalMs() {
		return peerEurekaStatusRefreshTimeIntervalMs;
	}

	public void setPeerEurekaStatusRefreshTimeIntervalMs(int peerEurekaStatusRefreshTimeIntervalMs) {
		this.peerEurekaStatusRefreshTimeIntervalMs = peerEurekaStatusRefreshTimeIntervalMs;
	}

	@Override
	public int getWaitTimeInMsWhenSyncEmpty() {
		return waitTimeInMsWhenSyncEmpty;
	}

	public void setWaitTimeInMsWhenSyncEmpty(int waitTimeInMsWhenSyncEmpty) {
		this.waitTimeInMsWhenSyncEmpty = waitTimeInMsWhenSyncEmpty;
	}

	@Override
	public int getPeerNodeConnectTimeoutMs() {
		return peerNodeConnectTimeoutMs;
	}

	public void setPeerNodeConnectTimeoutMs(int peerNodeConnectTimeoutMs) {
		this.peerNodeConnectTimeoutMs = peerNodeConnectTimeoutMs;
	}

	@Override
	public int getPeerNodeReadTimeoutMs() {
		return peerNodeReadTimeoutMs;
	}

	public void setPeerNodeReadTimeoutMs(int peerNodeReadTimeoutMs) {
		this.peerNodeReadTimeoutMs = peerNodeReadTimeoutMs;
	}

	@Override
	public int getPeerNodeTotalConnections() {
		return peerNodeTotalConnections;
	}

	public void setPeerNodeTotalConnections(int peerNodeTotalConnections) {
		this.peerNodeTotalConnections = peerNodeTotalConnections;
	}

	@Override
	public int getPeerNodeTotalConnectionsPerHost() {
		return peerNodeTotalConnectionsPerHost;
	}

	public void setPeerNodeTotalConnectionsPerHost(int peerNodeTotalConnectionsPerHost) {
		this.peerNodeTotalConnectionsPerHost = peerNodeTotalConnectionsPerHost;
	}

	@Override
	public int getPeerNodeConnectionIdleTimeoutSeconds() {
		return peerNodeConnectionIdleTimeoutSeconds;
	}

	public void setPeerNodeConnectionIdleTimeoutSeconds(int peerNodeConnectionIdleTimeoutSeconds) {
		this.peerNodeConnectionIdleTimeoutSeconds = peerNodeConnectionIdleTimeoutSeconds;
	}

	@Override
	public long getRetentionTimeInMSInDeltaQueue() {
		return retentionTimeInMSInDeltaQueue;
	}

	public void setRetentionTimeInMSInDeltaQueue(long retentionTimeInMSInDeltaQueue) {
		this.retentionTimeInMSInDeltaQueue = retentionTimeInMSInDeltaQueue;
	}

	@Override
	public long getDeltaRetentionTimerIntervalInMs() {
		return deltaRetentionTimerIntervalInMs;
	}

	public void setDeltaRetentionTimerIntervalInMs(long deltaRetentionTimerIntervalInMs) {
		this.deltaRetentionTimerIntervalInMs = deltaRetentionTimerIntervalInMs;
	}

	@Override
	public long getEvictionIntervalTimerInMs() {
		return evictionIntervalTimerInMs;
	}

	@Override
	public boolean shouldUseAwsAsgApi() {
		return this.useAwsAsgApi;
	}

	public void setUseAwsAsgApi(boolean useAwsAsgApi) {
		this.useAwsAsgApi = useAwsAsgApi;
	}

	public void setEvictionIntervalTimerInMs(long evictionIntervalTimerInMs) {
		this.evictionIntervalTimerInMs = evictionIntervalTimerInMs;
	}

	public int getASGQueryTimeoutMs() {
		return aSGQueryTimeoutMs;
	}

	public void setASGQueryTimeoutMs(int aSGQueryTimeoutMs) {
		this.aSGQueryTimeoutMs = aSGQueryTimeoutMs;
	}

	public long getASGUpdateIntervalMs() {
		return aSGUpdateIntervalMs;
	}

	public void setASGUpdateIntervalMs(long aSGUpdateIntervalMs) {
		this.aSGUpdateIntervalMs = aSGUpdateIntervalMs;
	}

	public long getASGCacheExpiryTimeoutMs() {
		return aSGCacheExpiryTimeoutMs;
	}

	public void setASGCacheExpiryTimeoutMs(long aSGCacheExpiryTimeoutMs) {
		this.aSGCacheExpiryTimeoutMs = aSGCacheExpiryTimeoutMs;
	}

	@Override
	public long getResponseCacheAutoExpirationInSeconds() {
		return responseCacheAutoExpirationInSeconds;
	}

	public void setResponseCacheAutoExpirationInSeconds(long responseCacheAutoExpirationInSeconds) {
		this.responseCacheAutoExpirationInSeconds = responseCacheAutoExpirationInSeconds;
	}

	@Override
	public long getResponseCacheUpdateIntervalMs() {
		return responseCacheUpdateIntervalMs;
	}

	public void setResponseCacheUpdateIntervalMs(long responseCacheUpdateIntervalMs) {
		this.responseCacheUpdateIntervalMs = responseCacheUpdateIntervalMs;
	}

	public boolean isUseReadOnlyResponseCache() {
		return useReadOnlyResponseCache;
	}

	public void setUseReadOnlyResponseCache(boolean useReadOnlyResponseCache) {
		this.useReadOnlyResponseCache = useReadOnlyResponseCache;
	}

	public boolean isDisableDelta() {
		return disableDelta;
	}

	public void setDisableDelta(boolean disableDelta) {
		this.disableDelta = disableDelta;
	}

	@Override
	public long getMaxIdleThreadInMinutesAgeForStatusReplication() {
		return maxIdleThreadInMinutesAgeForStatusReplication;
	}

	public void setMaxIdleThreadInMinutesAgeForStatusReplication(long maxIdleThreadInMinutesAgeForStatusReplication) {
		this.maxIdleThreadInMinutesAgeForStatusReplication = maxIdleThreadInMinutesAgeForStatusReplication;
	}

	@Override
	public int getMinThreadsForStatusReplication() {
		return minThreadsForStatusReplication;
	}

	public void setMinThreadsForStatusReplication(int minThreadsForStatusReplication) {
		this.minThreadsForStatusReplication = minThreadsForStatusReplication;
	}

	@Override
	public int getMaxThreadsForStatusReplication() {
		return maxThreadsForStatusReplication;
	}

	public void setMaxThreadsForStatusReplication(int maxThreadsForStatusReplication) {
		this.maxThreadsForStatusReplication = maxThreadsForStatusReplication;
	}

	@Override
	public int getMaxElementsInStatusReplicationPool() {
		return maxElementsInStatusReplicationPool;
	}

	public void setMaxElementsInStatusReplicationPool(int maxElementsInStatusReplicationPool) {
		this.maxElementsInStatusReplicationPool = maxElementsInStatusReplicationPool;
	}

	public boolean isSyncWhenTimestampDiffers() {
		return syncWhenTimestampDiffers;
	}

	public void setSyncWhenTimestampDiffers(boolean syncWhenTimestampDiffers) {
		this.syncWhenTimestampDiffers = syncWhenTimestampDiffers;
	}

	@Override
	public int getRegistrySyncRetries() {
		return registrySyncRetries;
	}

	public void setRegistrySyncRetries(int registrySyncRetries) {
		this.registrySyncRetries = registrySyncRetries;
	}

	@Override
	public long getRegistrySyncRetryWaitMs() {
		return registrySyncRetryWaitMs;
	}

	public void setRegistrySyncRetryWaitMs(long registrySyncRetryWaitMs) {
		this.registrySyncRetryWaitMs = registrySyncRetryWaitMs;
	}

	@Override
	public int getMaxElementsInPeerReplicationPool() {
		return maxElementsInPeerReplicationPool;
	}

	public void setMaxElementsInPeerReplicationPool(int maxElementsInPeerReplicationPool) {
		this.maxElementsInPeerReplicationPool = maxElementsInPeerReplicationPool;
	}

	@Override
	public long getMaxIdleThreadAgeInMinutesForPeerReplication() {
		return maxIdleThreadAgeInMinutesForPeerReplication;
	}

	public void setMaxIdleThreadAgeInMinutesForPeerReplication(long maxIdleThreadAgeInMinutesForPeerReplication) {
		this.maxIdleThreadAgeInMinutesForPeerReplication = maxIdleThreadAgeInMinutesForPeerReplication;
	}

	@Override
	public int getMinThreadsForPeerReplication() {
		return minThreadsForPeerReplication;
	}

	public void setMinThreadsForPeerReplication(int minThreadsForPeerReplication) {
		this.minThreadsForPeerReplication = minThreadsForPeerReplication;
	}

	@Override
	public int getMaxThreadsForPeerReplication() {
		return maxThreadsForPeerReplication;
	}

	public void setMaxThreadsForPeerReplication(int maxThreadsForPeerReplication) {
		this.maxThreadsForPeerReplication = maxThreadsForPeerReplication;
	}

	@Override
	public int getMaxTimeForReplication() {
		return maxTimeForReplication;
	}

	public void setMaxTimeForReplication(int maxTimeForReplication) {
		this.maxTimeForReplication = maxTimeForReplication;
	}

	public boolean isPrimeAwsReplicaConnections() {
		return primeAwsReplicaConnections;
	}

	public void setPrimeAwsReplicaConnections(boolean primeAwsReplicaConnections) {
		this.primeAwsReplicaConnections = primeAwsReplicaConnections;
	}

	public boolean isDisableDeltaForRemoteRegions() {
		return disableDeltaForRemoteRegions;
	}

	public void setDisableDeltaForRemoteRegions(boolean disableDeltaForRemoteRegions) {
		this.disableDeltaForRemoteRegions = disableDeltaForRemoteRegions;
	}

	@Override
	public int getRemoteRegionConnectTimeoutMs() {
		return remoteRegionConnectTimeoutMs;
	}

	public void setRemoteRegionConnectTimeoutMs(int remoteRegionConnectTimeoutMs) {
		this.remoteRegionConnectTimeoutMs = remoteRegionConnectTimeoutMs;
	}

	@Override
	public int getRemoteRegionReadTimeoutMs() {
		return remoteRegionReadTimeoutMs;
	}

	public void setRemoteRegionReadTimeoutMs(int remoteRegionReadTimeoutMs) {
		this.remoteRegionReadTimeoutMs = remoteRegionReadTimeoutMs;
	}

	@Override
	public int getRemoteRegionTotalConnections() {
		return remoteRegionTotalConnections;
	}

	public void setRemoteRegionTotalConnections(int remoteRegionTotalConnections) {
		this.remoteRegionTotalConnections = remoteRegionTotalConnections;
	}

	@Override
	public int getRemoteRegionTotalConnectionsPerHost() {
		return remoteRegionTotalConnectionsPerHost;
	}

	public void setRemoteRegionTotalConnectionsPerHost(int remoteRegionTotalConnectionsPerHost) {
		this.remoteRegionTotalConnectionsPerHost = remoteRegionTotalConnectionsPerHost;
	}

	@Override
	public int getRemoteRegionConnectionIdleTimeoutSeconds() {
		return remoteRegionConnectionIdleTimeoutSeconds;
	}

	public void setRemoteRegionConnectionIdleTimeoutSeconds(int remoteRegionConnectionIdleTimeoutSeconds) {
		this.remoteRegionConnectionIdleTimeoutSeconds = remoteRegionConnectionIdleTimeoutSeconds;
	}

	public boolean isgZipContentFromRemoteRegion() {
		return gZipContentFromRemoteRegion;
	}

	public void setgZipContentFromRemoteRegion(boolean gZipContentFromRemoteRegion) {
		this.gZipContentFromRemoteRegion = gZipContentFromRemoteRegion;
	}

	@Override
	public Map<String, String> getRemoteRegionUrlsWithName() {
		return remoteRegionUrlsWithName;
	}

	public void setRemoteRegionUrlsWithName(Map<String, String> remoteRegionUrlsWithName) {
		this.remoteRegionUrlsWithName = remoteRegionUrlsWithName;
	}

	@Override
	public String[] getRemoteRegionUrls() {
		return remoteRegionUrls;
	}

	public void setRemoteRegionUrls(String[] remoteRegionUrls) {
		this.remoteRegionUrls = remoteRegionUrls;
	}

	public Map<String, Set<String>> getRemoteRegionAppWhitelist() {
		return remoteRegionAppWhitelist;
	}

	public void setRemoteRegionAppWhitelist(Map<String, Set<String>> remoteRegionAppWhitelist) {
		this.remoteRegionAppWhitelist = remoteRegionAppWhitelist;
	}

	@Override
	public int getRemoteRegionRegistryFetchInterval() {
		return remoteRegionRegistryFetchInterval;
	}

	public void setRemoteRegionRegistryFetchInterval(int remoteRegionRegistryFetchInterval) {
		this.remoteRegionRegistryFetchInterval = remoteRegionRegistryFetchInterval;
	}

	@Override
	public int getRemoteRegionFetchThreadPoolSize() {
		return remoteRegionFetchThreadPoolSize;
	}

	public void setRemoteRegionFetchThreadPoolSize(int remoteRegionFetchThreadPoolSize) {
		this.remoteRegionFetchThreadPoolSize = remoteRegionFetchThreadPoolSize;
	}

	@Override
	public String getRemoteRegionTrustStore() {
		return remoteRegionTrustStore;
	}

	public void setRemoteRegionTrustStore(String remoteRegionTrustStore) {
		this.remoteRegionTrustStore = remoteRegionTrustStore;
	}

	@Override
	public String getRemoteRegionTrustStorePassword() {
		return remoteRegionTrustStorePassword;
	}

	public void setRemoteRegionTrustStorePassword(String remoteRegionTrustStorePassword) {
		this.remoteRegionTrustStorePassword = remoteRegionTrustStorePassword;
	}

	public boolean isDisableTransparentFallbackToOtherRegion() {
		return disableTransparentFallbackToOtherRegion;
	}

	public void setDisableTransparentFallbackToOtherRegion(boolean disableTransparentFallbackToOtherRegion) {
		this.disableTransparentFallbackToOtherRegion = disableTransparentFallbackToOtherRegion;
	}

	public boolean isBatchReplication() {
		return batchReplication;
	}

	public void setBatchReplication(boolean batchReplication) {
		this.batchReplication = batchReplication;
	}

	@Override
	public boolean isRateLimiterEnabled() {
		return rateLimiterEnabled;
	}

	public void setRateLimiterEnabled(boolean rateLimiterEnabled) {
		this.rateLimiterEnabled = rateLimiterEnabled;
	}

	@Override
	public boolean isRateLimiterThrottleStandardClients() {
		return rateLimiterThrottleStandardClients;
	}

	public void setRateLimiterThrottleStandardClients(boolean rateLimiterThrottleStandardClients) {
		this.rateLimiterThrottleStandardClients = rateLimiterThrottleStandardClients;
	}

	@Override
	public Set<String> getRateLimiterPrivilegedClients() {
		return rateLimiterPrivilegedClients;
	}

	public void setRateLimiterPrivilegedClients(Set<String> rateLimiterPrivilegedClients) {
		this.rateLimiterPrivilegedClients = rateLimiterPrivilegedClients;
	}

	@Override
	public int getRateLimiterBurstSize() {
		return rateLimiterBurstSize;
	}

	public void setRateLimiterBurstSize(int rateLimiterBurstSize) {
		this.rateLimiterBurstSize = rateLimiterBurstSize;
	}

	@Override
	public int getRateLimiterRegistryFetchAverageRate() {
		return rateLimiterRegistryFetchAverageRate;
	}

	public void setRateLimiterRegistryFetchAverageRate(int rateLimiterRegistryFetchAverageRate) {
		this.rateLimiterRegistryFetchAverageRate = rateLimiterRegistryFetchAverageRate;
	}

	@Override
	public int getRateLimiterFullFetchAverageRate() {
		return rateLimiterFullFetchAverageRate;
	}

	public void setRateLimiterFullFetchAverageRate(int rateLimiterFullFetchAverageRate) {
		this.rateLimiterFullFetchAverageRate = rateLimiterFullFetchAverageRate;
	}

	public boolean isLogIdentityHeaders() {
		return logIdentityHeaders;
	}

	public void setLogIdentityHeaders(boolean logIdentityHeaders) {
		this.logIdentityHeaders = logIdentityHeaders;
	}

	@Override
	public String getListAutoScalingGroupsRoleName() {
		return listAutoScalingGroupsRoleName;
	}

	public void setListAutoScalingGroupsRoleName(String listAutoScalingGroupsRoleName) {
		this.listAutoScalingGroupsRoleName = listAutoScalingGroupsRoleName;
	}

	public boolean isEnableReplicatedRequestCompression() {
		return enableReplicatedRequestCompression;
	}

	public void setEnableReplicatedRequestCompression(boolean enableReplicatedRequestCompression) {
		this.enableReplicatedRequestCompression = enableReplicatedRequestCompression;
	}

	public void setJsonCodecName(String jsonCodecName) {
		this.jsonCodecName = jsonCodecName;
	}

	public void setXmlCodecName(String xmlCodecName) {
		this.xmlCodecName = xmlCodecName;
	}

	@Override
	public int getRoute53BindRebindRetries() {
		return route53BindRebindRetries;
	}

	public void setRoute53BindRebindRetries(int route53BindRebindRetries) {
		this.route53BindRebindRetries = route53BindRebindRetries;
	}

	@Override
	public int getRoute53BindingRetryIntervalMs() {
		return route53BindingRetryIntervalMs;
	}

	public void setRoute53BindingRetryIntervalMs(int route53BindingRetryIntervalMs) {
		this.route53BindingRetryIntervalMs = route53BindingRetryIntervalMs;
	}

	@Override
	public long getRoute53DomainTTL() {
		return route53DomainTTL;
	}

	public void setRoute53DomainTTL(long route53DomainTTL) {
		this.route53DomainTTL = route53DomainTTL;
	}

	@Override
	public AwsBindingStrategy getBindingStrategy() {
		return bindingStrategy;
	}

	public void setBindingStrategy(AwsBindingStrategy bindingStrategy) {
		this.bindingStrategy = bindingStrategy;
	}

	public int getMinAvailableInstancesForPeerReplication() {
		return minAvailableInstancesForPeerReplication;
	}

	public void setMinAvailableInstancesForPeerReplication(int minAvailableInstancesForPeerReplication) {
		this.minAvailableInstancesForPeerReplication = minAvailableInstancesForPeerReplication;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EurekaServerConfigBean that = (EurekaServerConfigBean) o;
		return aSGCacheExpiryTimeoutMs == that.aSGCacheExpiryTimeoutMs && aSGQueryTimeoutMs == that.aSGQueryTimeoutMs
				&& aSGUpdateIntervalMs == that.aSGUpdateIntervalMs && Objects.equals(aWSAccessId, that.aWSAccessId)
				&& Objects.equals(aWSSecretKey, that.aWSSecretKey) && batchReplication == that.batchReplication
				&& bindingStrategy == that.bindingStrategy
				&& deltaRetentionTimerIntervalInMs == that.deltaRetentionTimerIntervalInMs
				&& disableDelta == that.disableDelta
				&& disableDeltaForRemoteRegions == that.disableDeltaForRemoteRegions
				&& disableTransparentFallbackToOtherRegion == that.disableTransparentFallbackToOtherRegion
				&& eIPBindingRetryIntervalMs == that.eIPBindingRetryIntervalMs
				&& eIPBindingRetryIntervalMsWhenUnbound == that.eIPBindingRetryIntervalMsWhenUnbound
				&& eIPBindRebindRetries == that.eIPBindRebindRetries
				&& enableReplicatedRequestCompression == that.enableReplicatedRequestCompression
				&& enableSelfPreservation == that.enableSelfPreservation
				&& evictionIntervalTimerInMs == that.evictionIntervalTimerInMs
				&& gZipContentFromRemoteRegion == that.gZipContentFromRemoteRegion
				&& Objects.equals(jsonCodecName, that.jsonCodecName)
				&& Objects.equals(listAutoScalingGroupsRoleName, that.listAutoScalingGroupsRoleName)
				&& logIdentityHeaders == that.logIdentityHeaders
				&& maxElementsInPeerReplicationPool == that.maxElementsInPeerReplicationPool
				&& maxElementsInStatusReplicationPool == that.maxElementsInStatusReplicationPool
				&& maxIdleThreadAgeInMinutesForPeerReplication == that.maxIdleThreadAgeInMinutesForPeerReplication
				&& maxIdleThreadInMinutesAgeForStatusReplication == that.maxIdleThreadInMinutesAgeForStatusReplication
				&& maxThreadsForPeerReplication == that.maxThreadsForPeerReplication
				&& maxThreadsForStatusReplication == that.maxThreadsForStatusReplication
				&& maxTimeForReplication == that.maxTimeForReplication
				&& minAvailableInstancesForPeerReplication == that.minAvailableInstancesForPeerReplication
				&& minThreadsForPeerReplication == that.minThreadsForPeerReplication
				&& minThreadsForStatusReplication == that.minThreadsForStatusReplication
				&& numberOfReplicationRetries == that.numberOfReplicationRetries
				&& peerEurekaNodesUpdateIntervalMs == that.peerEurekaNodesUpdateIntervalMs
				&& peerEurekaStatusRefreshTimeIntervalMs == that.peerEurekaStatusRefreshTimeIntervalMs
				&& peerNodeConnectionIdleTimeoutSeconds == that.peerNodeConnectionIdleTimeoutSeconds
				&& peerNodeConnectTimeoutMs == that.peerNodeConnectTimeoutMs
				&& peerNodeReadTimeoutMs == that.peerNodeReadTimeoutMs
				&& peerNodeTotalConnections == that.peerNodeTotalConnections
				&& peerNodeTotalConnectionsPerHost == that.peerNodeTotalConnectionsPerHost
				&& primeAwsReplicaConnections == that.primeAwsReplicaConnections
				&& Objects.equals(environment, that.environment) && rateLimiterBurstSize == that.rateLimiterBurstSize
				&& rateLimiterEnabled == that.rateLimiterEnabled
				&& rateLimiterFullFetchAverageRate == that.rateLimiterFullFetchAverageRate
				&& Objects.equals(rateLimiterPrivilegedClients, that.rateLimiterPrivilegedClients)
				&& rateLimiterRegistryFetchAverageRate == that.rateLimiterRegistryFetchAverageRate
				&& rateLimiterThrottleStandardClients == that.rateLimiterThrottleStandardClients
				&& registrySyncRetries == that.registrySyncRetries
				&& registrySyncRetryWaitMs == that.registrySyncRetryWaitMs
				&& Objects.equals(remoteRegionAppWhitelist, that.remoteRegionAppWhitelist)
				&& remoteRegionConnectionIdleTimeoutSeconds == that.remoteRegionConnectionIdleTimeoutSeconds
				&& remoteRegionConnectTimeoutMs == that.remoteRegionConnectTimeoutMs
				&& remoteRegionFetchThreadPoolSize == that.remoteRegionFetchThreadPoolSize
				&& remoteRegionReadTimeoutMs == that.remoteRegionReadTimeoutMs
				&& remoteRegionRegistryFetchInterval == that.remoteRegionRegistryFetchInterval
				&& remoteRegionTotalConnections == that.remoteRegionTotalConnections
				&& remoteRegionTotalConnectionsPerHost == that.remoteRegionTotalConnectionsPerHost
				&& Objects.equals(remoteRegionTrustStore, that.remoteRegionTrustStore)
				&& Objects.equals(remoteRegionTrustStorePassword, that.remoteRegionTrustStorePassword)
				&& Arrays.equals(remoteRegionUrls, that.remoteRegionUrls)
				&& Objects.equals(remoteRegionUrlsWithName, that.remoteRegionUrlsWithName)
				&& Double.compare(that.renewalPercentThreshold, renewalPercentThreshold) == 0
				&& renewalThresholdUpdateIntervalMs == that.renewalThresholdUpdateIntervalMs
				&& responseCacheAutoExpirationInSeconds == that.responseCacheAutoExpirationInSeconds
				&& responseCacheUpdateIntervalMs == that.responseCacheUpdateIntervalMs
				&& retentionTimeInMSInDeltaQueue == that.retentionTimeInMSInDeltaQueue
				&& route53BindingRetryIntervalMs == that.route53BindingRetryIntervalMs
				&& route53BindRebindRetries == that.route53BindRebindRetries
				&& route53DomainTTL == that.route53DomainTTL
				&& syncWhenTimestampDiffers == that.syncWhenTimestampDiffers
				&& useReadOnlyResponseCache == that.useReadOnlyResponseCache
				&& waitTimeInMsWhenSyncEmpty == that.waitTimeInMsWhenSyncEmpty
				&& Objects.equals(xmlCodecName, that.xmlCodecName)
				&& initialCapacityOfResponseCache == that.initialCapacityOfResponseCache
				&& expectedClientRenewalIntervalSeconds == that.expectedClientRenewalIntervalSeconds
				&& useAwsAsgApi == that.useAwsAsgApi && Objects.equals(myUrl, that.myUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aSGCacheExpiryTimeoutMs, aSGQueryTimeoutMs, aSGUpdateIntervalMs, aWSAccessId, aWSSecretKey,
				batchReplication, bindingStrategy, deltaRetentionTimerIntervalInMs, disableDelta,
				disableDeltaForRemoteRegions, disableTransparentFallbackToOtherRegion, eIPBindRebindRetries,
				eIPBindingRetryIntervalMs, eIPBindingRetryIntervalMsWhenUnbound, enableReplicatedRequestCompression,
				enableSelfPreservation, evictionIntervalTimerInMs, gZipContentFromRemoteRegion, jsonCodecName,
				listAutoScalingGroupsRoleName, logIdentityHeaders, maxElementsInPeerReplicationPool,
				maxElementsInStatusReplicationPool, maxIdleThreadAgeInMinutesForPeerReplication,
				maxIdleThreadInMinutesAgeForStatusReplication, maxThreadsForPeerReplication,
				maxThreadsForStatusReplication, maxTimeForReplication, minAvailableInstancesForPeerReplication,
				minThreadsForPeerReplication, minThreadsForStatusReplication, numberOfReplicationRetries,
				peerEurekaNodesUpdateIntervalMs, peerEurekaStatusRefreshTimeIntervalMs, peerNodeConnectTimeoutMs,
				peerNodeConnectionIdleTimeoutSeconds, peerNodeReadTimeoutMs, peerNodeTotalConnections,
				peerNodeTotalConnectionsPerHost, primeAwsReplicaConnections, environment, rateLimiterBurstSize,
				rateLimiterEnabled, rateLimiterFullFetchAverageRate, rateLimiterPrivilegedClients,
				rateLimiterRegistryFetchAverageRate, rateLimiterThrottleStandardClients, registrySyncRetries,
				registrySyncRetryWaitMs, remoteRegionAppWhitelist, remoteRegionConnectTimeoutMs,
				remoteRegionConnectionIdleTimeoutSeconds, remoteRegionFetchThreadPoolSize, remoteRegionReadTimeoutMs,
				remoteRegionRegistryFetchInterval, remoteRegionTotalConnections, remoteRegionTotalConnectionsPerHost,
				remoteRegionTrustStore, remoteRegionTrustStorePassword, remoteRegionUrls, remoteRegionUrlsWithName,
				renewalPercentThreshold, renewalThresholdUpdateIntervalMs, responseCacheAutoExpirationInSeconds,
				responseCacheUpdateIntervalMs, retentionTimeInMSInDeltaQueue, route53BindRebindRetries,
				route53BindingRetryIntervalMs, route53DomainTTL, syncWhenTimestampDiffers, useReadOnlyResponseCache,
				waitTimeInMsWhenSyncEmpty, xmlCodecName, initialCapacityOfResponseCache,
				expectedClientRenewalIntervalSeconds, useAwsAsgApi, myUrl);
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}

}
