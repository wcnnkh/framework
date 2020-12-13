package scw.event;

public interface EventType {
	public static final EventType CREATE = new DefaultEventType("CREATE");
	
	public static final EventType UPDATE = new DefaultEventType("UPDATE");
	
	public static final EventType DELETE = new DefaultEventType("DELETE");
	
	String getName();
	
	class DefaultEventType implements EventType{
		private String name;
		
		public DefaultEventType(String name){
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null){
				return false;
			}
			
			if(obj instanceof DefaultEventType){
				return ((DefaultEventType) obj).name.equals(name);
			}
			
			return false;
		}

		public String getName() {
			return name;
		}
	}
}
