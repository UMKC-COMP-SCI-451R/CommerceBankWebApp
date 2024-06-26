package com.commercebank;
import com.commercebank.accounts.Accounts;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

@SessionAttributes({"conversation","account"})
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
    public String askQuestion(@RequestBody String[] messages, HttpSession session) {
        if(OPENAI_API_KEY == null){
            return "No OpenAI api key found.";
        }else{
            try{
                //format messages of conversation
                ArrayList<String> formatMessageArray = new ArrayList<>();
                formatMessageArray.add(message("system",teach_model_about_the_web_app));
                Accounts sessionAccount = (Accounts) session.getAttribute("account");
                if(sessionAccount != null){
                    formatMessageArray.add(message("system","This user's name is " + sessionAccount.getFirstName()));
                    System.out.println(sessionAccount.getFirstName());
                }
                for(int i = 0; i <messages.length; i++)
                {
                    String cleanedMessage = messages[i].replaceAll("\"", "'").replaceAll("[\\n\\t\\f\\r]", " ").trim();
                    if(i % 2 == 0){
                        formatMessageArray.add(message("user",cleanedMessage));
                    }else formatMessageArray.add(message("assistant",cleanedMessage));
                }

                // send and get response
                String requestBody = "{\"model\":\"gpt-3.5-turbo-0125\",\"messages\":["+String.join(",",formatMessageArray)+"]}";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);
                String responseStr = response.getBody();

                // extract text content of the response
                JSONObject obj = new JSONObject(responseStr);
                JSONArray choices = obj.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String lastResponse = message.getString("content");
                    updateConversation(lastResponse,session,messages);
                    return lastResponse;
                }else{
                    return "No reponse";
                }
            }catch(Exception e){
                System.out.println(e);
                return "Something go wrong. Can't generate answer.";
            }


        }
    }

    @GetMapping("/getConversation")
    public ArrayList<String> fetchData(HttpSession session) {
//        Enumeration<String> attributeNames = session.getAttributeNames();
//        while (attributeNames.hasMoreElements()) {
//            String attributeName = attributeNames.nextElement();
//            Object attributeValue = session.getAttribute(attributeName);
//            System.out.println(attributeName + ": " + attributeValue);
//        }
        if(session.getAttribute("conversation") != null)
            return (ArrayList<String>) session.getAttribute("conversation"); // Spring automatically converts this list to JSON
        else return new ArrayList<>();
    }

    @GetMapping("/closeConversation")
    public String closeConversation(HttpSession session) {
        ArrayList<String> conver = (ArrayList<String>) session.getAttribute("conversation");
        conver.removeAll(conver);
        session.setAttribute("conversation",conver);
       return "conversation closed";
    }

    public void updateConversation(String lastResponse, HttpSession session, String[] messages){
        ArrayList<String> conversation = new ArrayList<>();
        if(session.getAttribute("conversation") != null)
            conversation = (ArrayList<String>) session.getAttribute("conversation");
        conversation.add(messages[messages.length-1]);
        conversation.add(lastResponse);
        session.setAttribute("conversation", conversation);
        //System.out.println(session.getAttribute("conversation"));
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
