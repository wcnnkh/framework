package scw.eureka.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.netflix.appinfo.AbstractEurekaIdentity;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaClientIdentity;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.util.RateLimiter;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerIdentity;
import com.netflix.eureka.RateLimitingFilter;
import com.netflix.eureka.util.EurekaMonitors;
import com.netflix.servo.monitor.DynamicCounter;
import com.netflix.servo.monitor.MonitorConfig;

import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpStatus;
import scw.http.server.HttpService;
import scw.http.server.HttpServiceInterceptor;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpRequestWrapper;
import scw.http.server.ServerHttpResponse;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class EurekaServiceInterceptor implements HttpServiceInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
	public static final String UNKNOWN = "unknown";
	private static final String NAME_PREFIX = "DiscoveryServerRequestAuth_Name_";
	private static final int SC_TEMPORARY_REDIRECT = 307;

	private static final Set<String> DEFAULT_PRIVILEGED_CLIENTS = new HashSet<String>(
			Arrays.asList(EurekaClientIdentity.DEFAULT_CLIENT_NAME, EurekaServerIdentity.DEFAULT_SERVER_NAME));

	private static final Pattern TARGET_RE = Pattern.compile("^.*/apps(/[^/]*)?$");

	enum Target {
		FullFetch, DeltaFetch, Application, Other
	}

	/**
	 * Includes both full and delta fetches.
	 */
	private static final RateLimiter registryFetchRateLimiter = new RateLimiter(TimeUnit.SECONDS);

	/**
	 * Only full registry fetches.
	 */
	private static final RateLimiter registryFullFetchRateLimiter = new RateLimiter(TimeUnit.SECONDS);

	private EurekaServerConfig serverConfig;

	public EurekaServiceInterceptor(EurekaServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void intercept(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService)
			throws IOException {
		InstanceInfo myInfo = ApplicationInfoManager.getInstance().getInfo();
		InstanceStatus status = myInfo.getStatus();
		if (status != InstanceStatus.UP) {
			response.sendError(SC_TEMPORARY_REDIRECT,
					"Current node is currently not ready to serve requests -- current status: " + status
							+ " - try another DS node: ");
			return;
		}

		logAuth(request);

		Target target = getTarget(request);
		if (target != Target.Other && isRateLimited(request, target)) {
			incrementStats(target);
			if (serverConfig.isRateLimiterEnabled()) {
				response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
				return;
			}
		}

		if (request.getMethod() == HttpMethod.GET) {
			String acceptEncoding = request.getHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
			if (acceptEncoding == null) {
				httpService.service(new GzipServerHttpRequest(request), response);
				return;
			}
			if (!acceptEncoding.contains("gzip")) {
				response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
				return;
			}
		}
		httpService.service(request, response);
	}

	protected void logAuth(ServerHttpRequest request) {
		if (serverConfig.shouldLogIdentityHeaders()) {
			String clientName = getHeader(request, AbstractEurekaIdentity.AUTH_NAME_HEADER_KEY);
			String clientVersion = getHeader(request, AbstractEurekaIdentity.AUTH_VERSION_HEADER_KEY);

			DynamicCounter.increment(MonitorConfig.builder(NAME_PREFIX + clientName + "-" + clientVersion).build());
		}
	}

	protected String getHeader(ServerHttpRequest request, String headerKey) {
		String value = request.getHeaders().getFirst(headerKey);
		return StringUtils.isEmpty(value) ? UNKNOWN : value;
	}

	private static Target getTarget(ServerHttpRequest request) {
		Target target = Target.Other;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String pathInfo = httpRequest.getRequestURI();

		if ("GET".equals(httpRequest.getMethod()) && pathInfo != null) {
			Matcher matcher = TARGET_RE.matcher(pathInfo);
			if (matcher.matches()) {
				if (matcher.groupCount() == 0 || matcher.group(1) == null || "/".equals(matcher.group(1))) {
					target = Target.FullFetch;
				} else if ("/delta".equals(matcher.group(1))) {
					target = Target.DeltaFetch;
				} else {
					target = Target.Application;
				}
			}
		}
		if (target == Target.Other) {
			logger.debug("URL path {} not matched by rate limiting filter", pathInfo);
		}
		return target;
	}

	private boolean isRateLimited(ServerHttpRequest request, Target target) {
		if (isPrivileged(request)) {
			logger.debug("Privileged {} request", target);
			return false;
		}
		if (isOverloaded(target)) {
			logger.debug("Overloaded {} request; discarding it", target);
			return true;
		}
		logger.debug("{} request admitted", target);
		return false;
	}

	private boolean isPrivileged(ServerHttpRequest request) {
		if (serverConfig.isRateLimiterThrottleStandardClients()) {
			return false;
		}
		Set<String> privilegedClients = serverConfig.getRateLimiterPrivilegedClients();
		String clientName = request.getHeaders().getFirst(AbstractEurekaIdentity.AUTH_NAME_HEADER_KEY);
		return privilegedClients.contains(clientName) || DEFAULT_PRIVILEGED_CLIENTS.contains(clientName);
	}

	private boolean isOverloaded(Target target) {
		int maxInWindow = serverConfig.getRateLimiterBurstSize();
		int fetchWindowSize = serverConfig.getRateLimiterRegistryFetchAverageRate();
		boolean overloaded = !registryFetchRateLimiter.acquire(maxInWindow, fetchWindowSize);

		if (target == Target.FullFetch) {
			int fullFetchWindowSize = serverConfig.getRateLimiterFullFetchAverageRate();
			overloaded |= !registryFullFetchRateLimiter.acquire(maxInWindow, fullFetchWindowSize);
		}
		return overloaded;
	}

	private void incrementStats(Target target) {
		if (serverConfig.isRateLimiterEnabled()) {
			EurekaMonitors.RATE_LIMITED.increment();
			if (target == Target.FullFetch) {
				EurekaMonitors.RATE_LIMITED_FULL_FETCH.increment();
			}
		} else {
			EurekaMonitors.RATE_LIMITED_CANDIDATES.increment();
			if (target == Target.FullFetch) {
				EurekaMonitors.RATE_LIMITED_FULL_FETCH_CANDIDATES.increment();
			}
		}
	}

	// For testing purposes
	static void reset() {
		registryFetchRateLimiter.reset();
		registryFullFetchRateLimiter.reset();
	}

	private static class GzipServerHttpRequest extends ServerHttpRequestWrapper {
		private HttpHeaders httpHeaders;

		public GzipServerHttpRequest(ServerHttpRequest targetRequest) {
			super(targetRequest);
			this.httpHeaders = new HttpHeaders();
			this.httpHeaders.putAll(targetRequest.getHeaders());
			this.httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
		}
	}
}
