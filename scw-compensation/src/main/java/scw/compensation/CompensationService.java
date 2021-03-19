package scw.compensation;

/**
 * 补偿服务
 * @author asus1
 *
 */
public interface CompensationService {
	/**
	 * 注册一个补偿任务
	 * @param task
	 * @return
	 * @throws CompensationException
	 */
	Compensator register(Task task) throws CompensationException;
}
