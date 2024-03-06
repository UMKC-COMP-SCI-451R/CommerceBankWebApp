package com.commercebank;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OpenAiTest {
    @Test
    public void read_file(){
        String filePath = "src/main/resources/static/AI_model_initial_instruction.txt";
        try {
            String content = Files.readString(Paths.get(filePath));
            System.out.println("File content:");
            System.out.println(content);
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
