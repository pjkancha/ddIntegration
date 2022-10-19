package aws.devopsguru.partner.datadog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	//Datadog API_KEY and APP_KEY are passed as environment variables, they are not part of code.
	
	private static final Logger LOGGER=LoggerFactory.getLogger(DataDog.class);
	
	public static Logger getLogger() {
		
		return LOGGER;
	}

}
