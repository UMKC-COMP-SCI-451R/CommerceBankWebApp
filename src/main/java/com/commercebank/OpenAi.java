package com.commercebank;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionChoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.theokanning.openai.service.OpenAiService;

@Service
public class OpenAi {

    private OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    public String generateText(String prompt) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Somebody once told me the world is gonna roll me")
                .model("babbage-002")
                        .echo(true)
                        .build();
//        CompletionResponse completion = openAI.createCompletion(completionRequest);
//        return completion.getChoices().get(0).getText();
        return service.createCompletion(completionRequest).getChoices().get(0).getText();
    }
}
