package io.albot.lims.report.config;

import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessToken {

	static RestTemplate restTemplate = new RestTemplate();

	public static String getToken(String username, String password, String tokenurl) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("grant_type", "password");
		requestBody.add("client_id", "springboot-services");
		requestBody.add("client_secret", "e1ebb656-63a2-47ce-aa8f-ea00ef4e39cb");
		requestBody.add("username", username);
		requestBody.add("password", password);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(requestBody,
				headers);
		String token = "";
		try {
			String uri = tokenurl; //"http://venti-dev.albot.io:8080/auth/realms/ventilator/protocol/openid-connect/token";
			ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			log.debug(res.getBody());
			String json = res.getBody().toString();
			JSONObject jsonObj = new JSONObject(json);
			log.debug("Token............" + jsonObj.getString("access_token"));
			token = jsonObj.getString("access_token");
		} catch (HttpClientErrorException exception) {
			log.debug("callToRestService Error :" + exception.getResponseBodyAsString());

		} catch (HttpStatusCodeException exception) {
			log.debug("callToRestService Error :" + exception.getResponseBodyAsString());

		} catch (Exception exception) {
			log.debug(exception.toString());
		}
		return token;
	}

}
