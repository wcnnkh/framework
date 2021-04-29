package scw.env;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.event.EmptyObservable;
import scw.event.Observable;
import scw.io.FileSystemResource;
import scw.io.FileSystemResourceLoader;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.event.ObservableProperties;
import scw.io.resolver.PropertiesResolver;
import scw.io.resolver.support.PropertiesResolvers;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.ClassLoaderProvider;
import scw.util.ConcurrentReferenceHashMap;
import scw.value.Value;

public class DefaultEnvironmentResourceLoader extends FileSystemResourceLoader implements EnvironmentResourceLoader{
	private static final String[] SUFFIXS = new String[] {"scw_res_suffix", "SHUCHAOWEN_CONFIG_SUFFIX", "resource.suffix"};
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);
	private final ConcurrentReferenceHashMap<String, Resource> cacheMap = new ConcurrentReferenceHashMap<String, Resource>();
	private final PropertiesResolvers propertiesResolvers = new PropertiesResolvers();
	private final BasicEnvironment environment;

	public DefaultEnvironmentResourceLoader(BasicEnvironment environment, ClassLoaderProvider classLoaderProvider){
		this.environment = environment;
		setClassLoaderProvider(classLoaderProvider);
	}
	
	public BasicEnvironment getEnvironment() {
		return environment;
	}

	private Resource getResourceByCache(String location) {
		Resource resource = cacheMap.get(location);
		if (resource == null) {
			resource = super.getResource(location);
			Resource cache = cacheMap.putIfAbsent(location, resource);
			if (cache != null) {
				resource = cache;
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("Find resource {} result {}", location, resource);
				}
			}
			if (resource == null) {
				cacheMap.putIfAbsent(location, Resource.NONEXISTENT_RESOURCE);
			}
		}
		return resource;
	}

	/**
	 * 可使用的资源别名，使用优先级从左到右
	 * 
	 * @return
	 */
	protected String[] getResourceEnvironmentalNames() {
		Value value = null;
		for(String suffix : SUFFIXS) {
			value = environment.getValue(suffix);
			if(value != null && !value.isEmpty()) {
				return value.getAsObject(String[].class);
			}
		}
		return StringUtils.MEPTY_ARRAY;
	}

	/**
	 * 预计使用的资源列表，返回的资源并不一定存在, 使用优先级从高到低
	 * 
	 * @param resourceName
	 * @return
	 */
	public List<String> getEnvironmentalResourceNameList(String resourceName) {
		String[] suffixs = getResourceEnvironmentalNames();
		String resourceNameToUse = environment.resolvePlaceholders(resourceName);
		if (ArrayUtils.isEmpty(suffixs)) {
			return Arrays.asList(resourceNameToUse);
		}

		List<String> list = new ArrayList<String>(suffixs.length + 1);
		for (int i = suffixs.length - 1; i >= 0; i--) {
			list.add(getEnvironmentalResourceName(resourceNameToUse, suffixs[i]));
		}
		list.add(resourceNameToUse);
		return list;
	}

	protected String getEnvironmentalResourceName(String resourceName, String evnironmental) {
		int index = resourceName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return resourceName + evnironmental;
		} else {
			return resourceName.substring(0, index) + evnironmental + resourceName.substring(index);
		}
	};
	
	@Override
	protected boolean ignoreClassPathResource(FileSystemResource resource) {
		return super.ignoreClassPathResource(resource) || resource.getPath().startsWith(environment.getWorkPath());
	}

	public Resource getResource(String location) {
		Resource[] resources = getResources(location);
		if(ArrayUtils.isEmpty(resources)){
			return null;
		}
		
		Resource resourceToUse = resources[resources.length - 1];
		for(Resource resource : resources){
			if(resource.exists()){
				resourceToUse = resource;
				break;
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Get resource [{}] result {}", resourceToUse);
		}
		return resourceToUse;
	}

	public Resource[] getResources(String locationPattern) {
		List<String> nameList = getEnvironmentalResourceNameList(locationPattern);
		List<Resource> resources = new ArrayList<Resource>(nameList.size());
		for (String name : nameList) {
			Resource res = getResourceByCache(name);
			if (res == null) {
				continue;
			}
			resources.add(res);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Get resources [{}] results {}", resources);
		}
		return resources.toArray(new Resource[0]);
	}

	public Observable<Properties> getProperties(String location) {
		return getProperties(this, location);
	}
	
	public Observable<Properties> getProperties(String location,
			@Nullable String charsetName) {
		return getProperties(this, location, charsetName);
	}
	
	public Observable<Properties> getProperties(String location,
			@Nullable Charset charset) {
		return getProperties(this, location, charset);
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location) {
		return getProperties(propertiesResolver, location, (String)null);
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable String charsetName) {
		return getProperties(propertiesResolver, location, charsetName == null? null:Charset.forName(charsetName));
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) {
		Resource[] resources = getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return new EmptyObservable<Properties>();
		}
		//颠倒一下，优先级高的覆盖优先级低的
		return new ObservableProperties(propertiesResolver, (Resource[])ArrayUtils.reversal(resources), charset);
	}
	
	public boolean exists(String location){
		return ResourceUtils.exists(this, location);
	}
	
	public boolean canResolveProperties(Resource resource) {
		return propertiesResolvers.canResolveProperties(resource);
	}

	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
		propertiesResolvers.resolveProperties(properties, resource, charset);
	}

	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		propertiesResolvers.addPropertiesResolver(propertiesResolver);
	}
}
