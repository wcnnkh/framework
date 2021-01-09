package scw.dom.append;

public class DefaultAppendChildServiceFactory extends AppendChildServiceFactory{

	public DefaultAppendChildServiceFactory(){
		services.add(new LastAppendChildService());
		services.add(new CollectionAppendChildService(this));
		services.add(new ArrayAppendChildService(this));
		services.add(new MapAppendChildService(this));
		services.add(new ToMapAppendChildService(this));
	}
	
}
