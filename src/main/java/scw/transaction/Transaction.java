package scw.transaction;

public interface Transaction extends SavepointManager {

	boolean isNewTransaction();

	boolean isActive();

	boolean hasSavepoint();
}
