package aws.devopsguru.partner.datadog;
import java.util.HashMap;
import java.util.Map;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.ApiException;
import com.datadog.api.client.v2.api.IncidentsApi;
import com.datadog.api.client.v2.model.IncidentCreateAttributes;
import com.datadog.api.client.v2.model.IncidentCreateData;
import com.datadog.api.client.v2.model.IncidentCreateRelationships;
import com.datadog.api.client.v2.model.IncidentCreateRequest;
import com.datadog.api.client.v2.model.IncidentFieldAttributes;
import com.datadog.api.client.v2.model.IncidentFieldAttributesSingleValue;
import com.datadog.api.client.v2.model.IncidentFieldAttributesSingleValueType;
import com.datadog.api.client.v2.model.IncidentResponse;
import com.datadog.api.client.v2.model.IncidentType;
import com.datadog.api.client.v2.model.IncidentUpdateData;
import com.datadog.api.client.v2.model.IncidentUpdateRelationships;
import com.datadog.api.client.v2.model.IncidentUpdateRequest;
import com.datadog.api.client.v2.model.IncidentsResponse;
import com.datadog.api.client.v2.model.NullableRelationshipToUser;
import com.datadog.api.client.v2.model.NullableRelationshipToUserData;
import com.datadog.api.client.v2.model.UsersType;
import com.fasterxml.jackson.databind.JsonNode;

public class DDIncidentType {

	
	public static void allFeatures(JsonNode jsonNode) {
		if (jsonNode.path("detail").path("messageType").asText().equals("NEW_INSIGHT")) 
			{
				DDIncidentType.newInsight(jsonNode);
			} 
		
		else if (jsonNode.path("detail").path("messageType").asText().equals("CLOSED_INSIGHT")) {
			try {
				DDIncidentType.closeInsight(jsonNode);
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		
		else if (jsonNode.path("detail").path("messageType").asText().equals("NEW_ASSOCIATION"))
			{
		    	DDIncidentType.newAssociation(jsonNode);
			}
			
		else if (jsonNode.path("detail").path("messageType").asText().equals("SEVERITY_UPGRADED"))
			{
			    DDIncidentType.SeverityUpgraded(jsonNode);
			}
			
			else if (jsonNode.path("detail").path("messageType").asText().equals("NEW_RECOMMENDATION"))
			{
				DDIncidentType.newRecommendation(jsonNode);
			}		
		
	}
	
	public static void newInsight(JsonNode input) {
		Constants.getLogger().info("Inside the new insight");
		ApiClient defaultClient = ApiClient.getDefaultApiClient();
	    defaultClient.setUnstableOperationEnabled("v2.createIncident", true);
	    IncidentsApi apiInstance = new IncidentsApi(defaultClient);
	    String USER_DATA_ID = System.getenv("USER_DATA_ID");
	    IncidentCreateRequest body =
		        new IncidentCreateRequest()
		            .data(
		                new IncidentCreateData()
		                    .type(IncidentType.INCIDENTS)
		                    .attributes(
		                        new IncidentCreateAttributes()
		                            .title(input.path("detail").path("insightDescription").asText())
		                            .customerImpacted(false)
		                            .fields(
		                                Map.ofEntries(
		                                    Map.entry(
		                                        "severity",
		                                        new IncidentFieldAttributes(
		                                            new IncidentFieldAttributesSingleValue()
		                                                .type(
		                                                    IncidentFieldAttributesSingleValueType.DROPDOWN)
		                                                .value("SEV-1"))),
		                                    
		                               Map.entry("summary",
			                                        new IncidentFieldAttributes(
			                                            new IncidentFieldAttributesSingleValue()
			                                                .type(
			                                                    IncidentFieldAttributesSingleValueType.TEXTBOX)
			                                                .value(input.path("detail").path("insightId").asText()))),
		                                Map.entry(
		                                        "state",
		                                        new IncidentFieldAttributes(
		                                            new IncidentFieldAttributesSingleValue()
		                                                .type(
		                                                    IncidentFieldAttributesSingleValueType.DROPDOWN)
		                                                .value("active"))))))			              
		                    .relationships(
		                        new IncidentCreateRelationships()
		                            .commanderUser(
		                                new NullableRelationshipToUser()
		                                    .data(
		                                        new NullableRelationshipToUserData()
		                                            .type(UsersType.USERS)
		                                            .id(USER_DATA_ID)))));

		    try {
		      IncidentResponse result = apiInstance.createIncident(body);
		      Constants.getLogger().info(result.toString());
		    } catch (ApiException e) {
			  Constants.getLogger().error("Exception when calling IncidentsApi#createIncident");
		      Constants.getLogger().error("Status code: " + e.getCode());
		      Constants.getLogger().error("Reason: " + e.getResponseBody());
		      Constants.getLogger().error("Response headers: " + e.getResponseHeaders());
		      e.printStackTrace();
		    }		
	}
	
	public static IncidentsResponse getAllIncidents() {
		Constants.getLogger().info("Inside the getAllIncidents");
	 ApiClient defaultClient = ApiClient.getDefaultApiClient();
	    defaultClient.setUnstableOperationEnabled("v2.listIncidents", true);
	    IncidentsApi apiInstance = new IncidentsApi(defaultClient);
	    IncidentsResponse result=null;
	    try {
	       result = apiInstance.listIncidents();
	    } catch (ApiException e) {
	    	 Constants.getLogger().error("Exception when calling listOfIncidents#");
	         Constants.getLogger().error("Status code: " + e.getCode());
	         Constants.getLogger().error("Reason: " + e.getResponseBody());
	         Constants.getLogger().error("Response headers: " + e.getResponseHeaders());
	      e.printStackTrace();
	    }
	  return result;
	  }

	public static void closeInsight(JsonNode input) throws ApiException {
		Constants.getLogger().info("Inside the closeinsight usecase");
		ApiClient defaultClient = ApiClient.getDefaultApiClient();
		defaultClient.setUnstableOperationEnabled("v2.deleteIncident", true);
		IncidentsApi apiInstance = new IncidentsApi(defaultClient);
		IncidentsResponse response=getAllIncidents();
		HashMap<String,String> mapping=new HashMap<String, String>();
		int num_of_incidents=response.getData().size();
		for(int i=0;i<num_of_incidents;i++) {
			String insight_id=response.getData().get(i).getAttributes().getFields().get("summary").getIncidentFieldAttributesSingleValue().getValue();
			String incident_id=response.getData().get(i).getId();
			Constants.getLogger().debug("Here is the incident_id" + incident_id +", "+ "Here is the insight ID: "+ insight_id );
			mapping.put(insight_id, incident_id);
		}		    
		try {
			String INCIDENT_DATA_ID =mapping.get(input.path("detail").path("insightId").asText());
			apiInstance.deleteIncident(INCIDENT_DATA_ID);
        	} catch (ApiException e) {
        		Constants.getLogger().error("Exception when calling IncidentsApi#deleteIncident");
        		Constants.getLogger().error("Status code: " + e.getCode());
        		Constants.getLogger().error("Reason: " + e.getResponseBody());
        		Constants.getLogger().error("Response headers: " + e.getResponseHeaders());
        		e.printStackTrace();
        }
  }

	public static void SeverityUpgraded(JsonNode input) {
		ApiClient defaultClient = ApiClient.getDefaultApiClient();
		defaultClient.setUnstableOperationEnabled("v2.createIncident", true);
		IncidentsApi apiInstance = new IncidentsApi(defaultClient);
		String USER_DATA_ID = System.getenv("USER_DATA_ID");
		//Make modifications to this 
		IncidentUpdateRequest body =
				new IncidentUpdateRequest()
            .data(
                new IncidentUpdateData()
                    .id(input.path("detail").path("insightId").asText())
                    .type(IncidentType.INCIDENTS)
                    .relationships(
                        new IncidentUpdateRelationships()
                            .commanderUser(
                                new NullableRelationshipToUser()
                                    .data(
                                        new NullableRelationshipToUserData()
                                            .id(USER_DATA_ID)
                                            .type(UsersType.USERS)))));

		try {
			IncidentResponse result = apiInstance.updateIncident(input.path("detail").path("insightId").asText(), body);
			Constants.getLogger().info(result.toString());
		} catch (ApiException e) {
    	  Constants.getLogger().error("Exception when calling UpdatingIncident#SeverityUpgraded");
          Constants.getLogger().error("Status code: " + e.getCode());
          Constants.getLogger().error("Reason: " + e.getResponseBody());
          Constants.getLogger().error("Response headers: " + e.getResponseHeaders());
          e.printStackTrace();
		}
  }


	public static void newAssociation(JsonNode jsonNode) {
		//code to be written
	}
	
	public static void InsightOpenAndAnomolies(JsonNode jsonNode) {
		//code to be written
	}

	public static void InsightOpenRecommendations(JsonNode jsonNode) {
		//code to be written
	}
	public static void newRecommendation(JsonNode jsonNode) {
		//code to be written
	}
}
