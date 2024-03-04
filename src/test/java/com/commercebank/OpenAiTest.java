package com.commercebank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class OpenAiTest {


    @Test
    public void testOpenAi(){
        OpenAi openAi = new OpenAi();
        String response = openAi.generateText("how do cook chicken?");
        System.out.println(response);
    }

}
