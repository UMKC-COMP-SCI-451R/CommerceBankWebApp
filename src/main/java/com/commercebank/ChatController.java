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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
public class ChatController {
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private final RestTemplate restTemplate;

    private final String teach_model_about_the_web_app = read_file("src/main/resources/static/AI_model_initial_instruction.txt").replaceAll("\"", "'").replaceAll("[\\n\\t\\f\\r]", " ").trim();
    public ChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestBody String conversation) {
        if(OPENAI_API_KEY == null){
            return "No OpenAI api key found.";
        }else{
            try{
                conversation = conversation.replaceAll("\"", "'").replaceAll("[\\n\\t\\f\\r]", " ").trim();
                //System.out.println(conversation);
                String regex = "You: |AI: ";
                String[] messageArray = conversation.split(regex); //this array have "" at index 0
                ArrayList<String> formatMessageArray = new ArrayList<>();
                formatMessageArray.add(message("user",teach_model_about_the_web_app));
                for(int i = 1; i <messageArray.length; i++) // i=1 to avoid the empty string at index 0
                {
                    if(i % 2 == 1){
                        formatMessageArray.add(message("user",messageArray[i].trim()));
                    }else formatMessageArray.add(message("assistant",messageArray[i].trim()));
                }
                String cleanedConversation = (teach_model_about_the_web_app + conversation).replaceAll("[\\n\\t\\f\\r]", " ");
                //System.out.println(cleanedConversation);
                String requestBody = "{\"model\":\"gpt-3.5-turbo-0125\",\"messages\":["+String.join(",",formatMessageArray)+"]}";
                System.out.println(requestBody);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                //System.out.println(entity);
                ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);
                String responseStr = response.getBody();
                //System.out.println(responseStr);
                JSONObject obj = new JSONObject(responseStr);
                JSONArray choices = obj.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    return message.getString("content");
                }else{
                    return "No reponse";
                }

            }catch(Exception e){
                System.out.println(e);
                return "Something go wrong. Can't generate answer.";
            }


        }
    }

    public String read_file(String filePath){
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            return "";
        }
    }

    public String message(String role, String content){
        return String.format("{ \"role\": \"%s\", \"content\": \"%s\"}",role,content);
    }

}
