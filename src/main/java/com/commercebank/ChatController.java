package com.commercebank;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ChatController {
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private final RestTemplate restTemplate;

    public ChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        if(OPENAI_API_KEY == null){
            return "No API KEY found";
        }else{
            System.out.println(question);
            String requestBody = "{\"model\":\"gpt-3.5-turbo-0125\",\"messages\":[{\"role\":\"user\",\"content\":\"" + question + "\"}]}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            System.out.println(entity);
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);
            String responseStr = response.getBody();
            System.out.println(responseStr);
            JSONObject obj = new JSONObject(responseStr);
            JSONArray choices = obj.getJSONArray("choices");
            if (!choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            }else{
                return "No reponse";
            }

            //return "print entity";
        }
    }

}
