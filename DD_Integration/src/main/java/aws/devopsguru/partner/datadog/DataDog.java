package aws.devopsguru.partner.datadog;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataDog implements RequestHandler<Map<String, Object>, String> {
	
	enum useCase{
		ALLEVENTS,
		ANOMOLIES,
		RECOMMENDATIONS,
		SEVERITYUPGRADED,	
	}
	
		public String handleRequest(Map<String, Object> input, Context context) {
			Constants.getLogger().info("Inside the Datadog handleRequest");
			// Take the object and make a string representation of it
			ObjectMapper objectMapper = new ObjectMapper();
			String temp = "";
			try {
				temp = objectMapper.writeValueAsString(input);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Make a json out of that string
			JsonNode jsonNode = null;
			try {
				jsonNode = objectMapper.readTree(temp);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			useCase caseChoice=useCase.ALLEVENTS;
			pickUseCase(caseChoice,jsonNode);
			
			return null;
		}
		
		public void pickUseCase(useCase caseChoice, JsonNode jsonNode) {
			
			switch(caseChoice)
			{
				case ALLEVENTS:
					Constants.getLogger().info("Inside all features pickUsecase");
					DDIncidentType.allFeatures(jsonNode);
					break;
					
				case ANOMOLIES:
					DDIncidentType.InsightOpenAndAnomolies(jsonNode);
					break;
					
				case RECOMMENDATIONS:
					DDIncidentType.InsightOpenRecommendations(jsonNode);
					break;
					
				case SEVERITYUPGRADED:
					DDIncidentType.SeverityUpgraded(jsonNode);
					break;		
			}		
		}
}
