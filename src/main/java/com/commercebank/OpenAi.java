package com.commercebank;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionChoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.theokanning.openai.service.OpenAiService;

@Service
public class OpenAi {
    private OpenAiService service = new OpenAiService("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");

    public String generateText(String prompt) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Somebody once told me the world is gonna roll me")
                .model("gpt-3.5-turbo-0125")
                        .echo(true)
                        .build();
//        CompletionResponse completion = openAI.createCompletion(completionRequest);
//        return completion.getChoices().get(0).getText();
        return service.createCompletion(completionRequest).getChoices().get(0).getText();
    }
}
