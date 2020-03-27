package scw.zookeeper;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.io.resource.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.NetworkUtils;

public final class ZookeeperUtils {
	private static Logger logger = LoggerUtils.getLogger(ZookeeperUtils.class);

	private ZookeeperUtils() {
	};

	public static ServerConfig getServerConfig(String path) throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getProperties(path, Constants.DEFAULT_CHARSET_NAME);
		return createServletConfig(properties);
	}

	public static ServerConfig createServletConfig(Properties properties) throws IOException, ConfigException {
		QuorumPeerConfig quorumPeerConfig = new QuorumPeerConfig();
		quorumPeerConfig.parseProperties(properties);
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.readFrom(quorumPeerConfig);
		return serverConfig;
	}

	public static void run(int port) throws Exception {
		if (!NetworkUtils.checkLocalPortCccupied(port)) {
			logger.warn("端口号已被占用：{}", port);
			return;
		}

		Properties properties = new Properties();
		File file = new File(GlobalPropertyFactory.getInstance().getWorkPath() + File.separator + "zk_data");
		if (!file.exists()) {
			file.mkdir();
		}
		properties.setProperty("dataDir", file.getPath());
		file = new File(GlobalPropertyFactory.getInstance().getWorkPath() + File.separator + "zk_logs");
		if (!file.exists()) {
			file.mkdir();
		}
		properties.setProperty("dataLogDir", file.getPath());
		properties.setProperty("clientPort", port + "");
		ServerConfig serverConfig = createServletConfig(properties);
		ZooKeeperServerMain zooKeeperServerMain = new ZooKeeperServerMain();
		zooKeeperServerMain.runFromConfig(serverConfig);
	}

	public static void run() throws Exception {
		run(2181);
	}
}
