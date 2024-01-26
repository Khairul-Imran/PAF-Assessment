package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {

		String url = UriComponentsBuilder
			.fromUriString("https://api.frankfurter.app/latest")
			.queryParam("amount", amount)
			.queryParam("from", from)
			.queryParam("to", to)
			.toUriString();
		
		RequestEntity<Void> request = RequestEntity
        	.get(url)
            .build();

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = template.exchange(request, String.class);

		if (response == null) {
			return -1000f;
		}
		
		String payload = response.getBody();
		
		JsonReader reader = Json.createReader(new StringReader(payload));
		JsonObject object = reader.readObject();
		
		JsonObject exchangedValue = object.getJsonObject("rates");
		float exchangedValueExtracted = exchangedValue.getInt("SGD");

		return exchangedValueExtracted;

	}
}
