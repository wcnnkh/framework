package run.soeasy.framework.core.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 一个实例工厂
 * 
 * @author wcnnkh
 *
 */
public interface InstanceFactory {
	boolean canInstantiated(@NonNull TypeDescriptor requiredType);

	Object newInstance(@NonNull TypeDescriptor requiredType);
}
