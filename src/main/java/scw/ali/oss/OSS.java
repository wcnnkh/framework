package scw.ali.oss;

import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PolicyConditions;

import scw.common.exception.AlreadyExistsException;
import scw.common.exception.NotFoundException;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.common.utils.XUtils;
import scw.net.http.enums.ProtocolType;

public final class OSS {
	private static final int DEFAULT_EXPIRE = 120;
	private final boolean debug;
	private final String debugPrefix;
	private final OSSClient ossClient;
	private final Map<String, String> bucketMap = new HashMap<String, String>();
	
	public OSS(String endpoint, String accessKeyId, String secretAccessKey, boolean debug){
		this(endpoint, accessKeyId, secretAccessKey, debug, "debug/");
	}
	
	public OSS(String endpoint, String accessKeyId, String secretAccessKey, boolean debug, String debugPrefix){
		this.debug = debug;
		this.debugPrefix = debugPrefix;
		
		DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, secretAccessKey);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		this.ossClient = new OSSClient(endpoint, provider, clientConfiguration);
	}
	
	public synchronized void registerBucket(String bucketName, String url){
		if(bucketMap.containsKey(bucketName)){
			throw new AlreadyExistsException(bucketName);
		}
		
		bucketMap.put(bucketName, url);
	}
	
	/**
	 * 判断是否是一个CDN地址
	 * @param url
	 * @return
	 */
	public boolean isBucketURL(String bucketUrl) {
		ProtocolType protocolType = ProtocolType.getHttpProtocolType(bucketUrl);
		if(protocolType == null){
			return false;
		}
		
		String u = bucketUrl.substring(protocolType.getValue().length());
		for(Entry<String, String> entry : bucketMap.entrySet()){
			if(u.startsWith(entry.getValue())){
				return true;
			}
		}
		return false;
	}
	
	public String getObjectKey(String bucketUrl){
		ProtocolType protocolType = ProtocolType.getHttpProtocolType(bucketUrl);
		if(protocolType == null){
			return null;
		}
		
		String u = bucketUrl.substring(protocolType.getValue().length());
		for(Entry<String, String> entry : bucketMap.entrySet()){
			if(u.startsWith(entry.getValue())){
				return u.substring(entry.getValue().length() + 1);
			}
		}
		return null;
	}
	
	public String getUrl(ProtocolType protocol, String bucketName, String objectKey) {
		if (protocol == null || StringUtils.isNull(bucketName, objectKey)) {
			throw new NullPointerException();
		}
		
		String url = bucketMap.get(bucketName);
		if(url == null){
			throw new NotFoundException("bucketName=" + bucketName);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(protocol.getValue());
		sb.append(url);
		sb.append("/");
		sb.append(objectKey);
		return sb.toString();
	}

	public OSSClient getOssClient() {
		return ossClient;
	}
	
	public void deleteObjectKey(String bucketName, String objectKey) {
		if(StringUtils.isNull(objectKey)){
			return ;
		}
		
		if(!bucketMap.containsKey(bucketName)){
			throw new NotFoundException("bucketName=" + bucketName);
		}
		
		if(debug){
			if(objectKey.startsWith(debugPrefix)){
				ossClient.deleteObject(bucketName, objectKey);
			}
		}else{
			ossClient.deleteObject(bucketName, objectKey);
		}
	}
	
	public void deleteByURL(String bucketUrl) {
		if(StringUtils.isNull(bucketUrl)){
			return ;
		}
		
		ProtocolType protocolType = ProtocolType.getHttpProtocolType(bucketUrl);
		if(protocolType == null){
			return ;
		}
		
		String u = bucketUrl.substring(protocolType.getValue().length());
		for(Entry<String, String> entry : bucketMap.entrySet()){
			if(u.startsWith(entry.getValue())){
				String objectKey = u.substring(entry.getValue().length() + 1);
				deleteObjectKey(entry.getKey(), objectKey);
			}
		}
	}
	
	public String newObjectKey(String key){
		if(debug){
			return debugPrefix + key;
		}else{
			return key;
		}
	}
	
	public String newObjectKey(String root, long uid, String suffix){
		StringBuilder sb = new StringBuilder();
		if(!StringUtils.isNull(root)){
			sb.append(root);
			sb.append("/");
		}
		sb.append(uid);
		sb.append("/");
		sb.append(XTime.format(System.currentTimeMillis(), "yyyy/MM/dd"));
		sb.append("/");
		sb.append(XUtils.getUUID());
		sb.append(StringUtils.complemented(Long.toString(System.currentTimeMillis(), 32), '0', 13));
		if(!StringUtils.isNull(suffix)){
			sb.append(".");
			sb.append(suffix);
		}
		return newObjectKey(sb.toString());
	}
	
	public boolean checkObjectKey(String objectKey, String root, long uid){
		StringBuilder sb = new StringBuilder();
		if(debug){
			sb.append(debugPrefix);
		}
		sb.append(root);
		sb.append("/");
		sb.append(uid);
		sb.append("/");
		return objectKey.startsWith(sb.toString());
	}
	
	public String getUrlAndCheck(ProtocolType protocol, String bucketName, String root, long uid, String objectKey) {
		if (protocol == null || StringUtils.isNull(bucketName, objectKey)) {
			throw new NullPointerException();
		}
		
		String url = bucketMap.get(bucketName);
		if(url == null){
			throw new NotFoundException("bucketName=" + bucketName);
		}
		
		if(!checkObjectKey(objectKey, root, uid) || isBucketURL(objectKey)){
			throw new ShuChaoWenRuntimeException("不合法的objectKey:" + objectKey);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(protocol.getValue());
		sb.append(url);
		sb.append("/");
		sb.append(objectKey);
		return sb.toString();
	}
	
	public ObjectListing listObject(String bucketName, String prefix, int limit, String nextMarker){
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketName);
		String newPrefix = debug? debugPrefix + prefix:prefix;
		listObjectsRequest.setPrefix(newPrefix);
		if(!StringUtils.isNull(nextMarker)){
			listObjectsRequest.setMarker(nextMarker);
		}
		
		listObjectsRequest.setMaxKeys(limit);
		return ossClient.listObjects(listObjectsRequest);
	}
	
	/**
	 * 此方法返回的可以序列化
	 * @param bucketName
	 * @param prefix
	 * @param limit
	 * @param nextMarker
	 * @return
	 */
	public scw.ali.oss.ObjectListing myListObject(String bucketName, String prefix, int limit, String nextMarker){
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketName);
		String newPrefix = debug? debugPrefix + prefix:prefix;
		listObjectsRequest.setPrefix(newPrefix);
		if(!StringUtils.isNull(nextMarker)){
			listObjectsRequest.setMarker(nextMarker);
		}
		
		listObjectsRequest.setMaxKeys(limit);
		ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
		return objectListing == null? null:new scw.ali.oss.ObjectListing(objectListing);
	}
	
	public void shutdown(){
		ossClient.shutdown();
	}

	public boolean isDebug() {
		return debug;
	}

	public String getDebugPrefix() {
		return debugPrefix;
	}
	
	private String getPostPolicy(String objectKey, Date expire){
		PolicyConditions policyConditions = new PolicyConditions();
		policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
		policyConditions.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, objectKey);
		return ossClient.generatePostPolicy(expire, policyConditions); 
	}
	
	public PostPolicySignature getPostPolicySignatureByUrl(String bucketUrl, String root, long uid){
		return getPostPolicySignatureByUrl(bucketUrl, root, uid, null, DEFAULT_EXPIRE);
	}
	
	public PostPolicySignature getPostPolicySignatureByUrl(String bucketUrl, String root, long uid, String suffix, int expire){
		if(StringUtils.isNull(bucketUrl)){
			return getPostPolicySignature(root, uid, suffix, expire);
		}
		
		String objectKey = getObjectKey(bucketUrl);
		if(objectKey == null){
			return getPostPolicySignature(root, uid, suffix, expire);
		}else{
			return getPostPolicySignature(objectKey, expire);
		}
	}
	
	public PostPolicySignature getPostPolicySignature(String root, long uid){
		return getPostPolicySignature(newObjectKey(root, uid, null), DEFAULT_EXPIRE);
	}
	
	public PostPolicySignature getPostPolicySignature(String root, long uid, String suffix, int expire){
		return getPostPolicySignature(newObjectKey(root, uid, suffix), expire);
	}
	
	/**
	 * @param objectKey
	 * @param expire 过期时间  单位是秒
	 * @return
	 */
	public PostPolicySignature getPostPolicySignature(String objectKey, int expire){
		long expireEndTime = System.currentTimeMillis() + expire * 1000;
		String policy = getPostPolicy(objectKey, new Date(expireEndTime));
		byte[] binaryData;
		try {
			binaryData = policy.getBytes(DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
        String encPolicy = BinaryUtil.toBase64String(binaryData);
        
		String postSignature = ossClient.calculatePostSignature(policy);
		PostPolicySignature signature = new PostPolicySignature();
		signature.setAccessId(ossClient.getCredentialsProvider().getCredentials().getAccessKeyId());
		signature.setPolicy(encPolicy);
		signature.setSignature(postSignature);
		signature.setKey(objectKey);
		return signature;
	}
}
