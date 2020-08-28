package scw.alibaba.oss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ObjectListing implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * A list of summary information describing the objects stored in the bucket
     */
    private List<OSSObjectSummary> objectSummaries = new ArrayList<OSSObjectSummary>();

    private List<String> commonPrefixes = new ArrayList<String>();

    private String bucketName;

    private String nextMarker;

    private boolean isTruncated;

    private String prefix;

    private String marker;

    private int maxKeys;

    private String delimiter;

    private String encodingType;
    
    public ObjectListing(){}//为了序列化
    
    public ObjectListing(com.aliyun.oss.model.ObjectListing objectListing){
    	if(objectListing.getObjectSummaries() != null){
    		for(com.aliyun.oss.model.OSSObjectSummary ossObjectSummary : objectListing.getObjectSummaries()){
    			if(ossObjectSummary == null){
    				continue;
    			}
    			
    			objectSummaries.add(new OSSObjectSummary(ossObjectSummary));
    		}
    	}
    	this.commonPrefixes = objectListing.getCommonPrefixes();
    	this.bucketName = objectListing.getBucketName();
    	this.nextMarker = objectListing.getNextMarker();
    	this.isTruncated = objectListing.isTruncated();
    	this.prefix = objectListing.getPrefix();
    	this.marker = objectListing.getMarker();
    	this.maxKeys = objectListing.getMaxKeys();
    	this.delimiter = objectListing.getDelimiter();
    	this.encodingType = objectListing.getEncodingType();
    }

    public List<OSSObjectSummary> getObjectSummaries() {
		return objectSummaries;
	}

	public void setObjectSummaries(List<OSSObjectSummary> objectSummaries) {
		this.objectSummaries = objectSummaries;
	}

	public void clearObjectSummaries() {
        this.objectSummaries.clear();
    }

    public List<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void addCommonPrefix(String commonPrefix) {
        this.commonPrefixes.add(commonPrefix);
    }

    public void setCommonPrefixes(List<String> commonPrefixes) {
        this.commonPrefixes.clear();
        if (commonPrefixes != null && !commonPrefixes.isEmpty()) {
            this.commonPrefixes.addAll(commonPrefixes);
        }
    }

    public void clearCommonPrefixes() {
        this.commonPrefixes.clear();
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }
}
