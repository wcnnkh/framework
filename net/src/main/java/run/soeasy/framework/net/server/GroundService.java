package run.soeasy.framework.net.server;

import java.io.IOException;

/**
 * 兜底服务
 * 
 * @author alisa
 *
 */
public interface GroundService extends Service {
	@Override
	void service(ServerRequest request, ServerResponse response) throws IOException, ServerException;
}
