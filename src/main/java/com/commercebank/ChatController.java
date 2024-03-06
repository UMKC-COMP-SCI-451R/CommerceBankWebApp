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

    private final String teach_model_about_the_web_app = "I’m building an online banking web application for Commerce Bank and I’m implementing a chat bot feature. I want you to be a virtual assistant who answers questions from the user based on the web app’s information I give you below. Here is everything you need to know about the web app:\n" +
            "1.\tLogin page/Home page: a place where users can login using their email and password. The login page is also the home page of the application. There is a login form on the right side of the screen so that the user can put in email and password to login with the Login button. Below that, there is a checkbox with a label called " +
            "“Remember me” which allows users to save their email inside their browser for the next time they login. Under that, there is a link called “Forget password”, the user can click to it in case they forgot their password and want to reset it. Also there is a link for registering in case they are new users of the web app.\n" +
            "2.\tSummary page: User must login to access this page. This page is where a user can view some account information like customer’s name, account ID number, routing number, account type. They can also view their current balance located on the left side of the screen. On the right side of the screen, they can view their transactions." +
            " By default, five recent transactions will be available to view. However, under that list, there is a filter section which allows them to select the date range and view the transactions happening between that date range.\n" +
            "3.\tTransfer page: User must login to access this page. This page is where a user makes transfers between bank accounts. There are three tabs on this page. The first tab is called “Make Transfer”. By clicking this tab, the user can view a form that the user can select the source account and destination account, and type in the amount " +
            "in dollars that they want to transfer and then click Send. If there isn’t an account that they want to select in the source and destination list, they can click the second tab called “Add External Account” to add a new external account. After putting information into the form like Bank Name, Routing Number, Account Number." +
            " They can click Add to add a new account. The new external account will need to be verified before use. To do that, the user can click the third tab called “Verify External Account”. Then, they can select the external account they want to verify and put some security deposit amount into the deposit input boxes. After that, they can click Verify." +
            " Once the external account is verified, it will show up in the list of source and destination accounts in the “Make Transfer” tab.\n" +
            "4.\tLoan page: User must login to access this page. This page is where a user can view some loan options. On the top of the page, there are 4 cards that show 4 different kinds of loan. There are “Credit Cards”, “Auto Loan”, “Home Loan”, and “Personal Loan”. We don't have any other loans. There is a “Learn More” button inside of those cards so that users can click on it to view" +
            " the details about a specific loan. Below these cards, there is a section to show the details of a loan. Once the Learn More button is clicked, the corresponding details will show up in the detail section. Those details are about some benefits and average interest rate of the loan. Inside of the detail section, there is a button called “Quick Apply”." +
            " The user can click this button if they are interested in this loan. After clicking Quick Apply, an input form will popup and ask for the credit score and the loan amount. After filling out the form, they can click Apply to officially apply for the loan.\n" +
            "5.\tProfile page: User must login to access this page. This page is where users can view and change their personal information and account’s setting. On the left of the page there is a card that contains personal information like Name, Address, Card Number, Phone number, and Password. The user can change the Address, Phone number or Password by clicking to the icon of a pen." +
            " After the pen icon is clicked, the input box for new information will show up. The user can type in the new information and click Save to save or Cancel to cancel the update. On the right side of the page, there is a card that shows some account’s settings: Multi-factor Authentication, Paperless, Text Alert, Email Alert along with check boxes." +
            " To make changes to current settings, the user needs to click on the Edit button located on top of the card, then the check boxes will be enabled for editing. After changes are made, the user needs to click the Save button located next to the Edit button to apply the changes.\n" +
            "6.\tForgot password page: After the user clicks the forgot password link on the login page/home page, the user will be redirected to the Forgot Password page. A security code will be sent to their email address as well. Inside of this page, there is a form so that the user can type in the security code and the new password." +
            " After filling out the form, the users can click a button called “Verify and Reset” to reset the password.\n" +
            "7.\tMulti Factor Authentication page: If the account setting of Multi-factor Authentication is enable, when the user click Login on the form in the Login Page, an email with the security code will be sent to their email and they will be redirected to this Multi Factor Authentication page. In this page, there is a form that contains an input box for the code." +
            " The user needs to type in the correct code before it expires and click Verify in order to log in to their account.\n" +
            "8.\tRegister Page: This page is for new users to sign up for this web application. On the Login Page, after clicking the register link or button, they will be redirected to this page. On this page there is a form for all essential information. They can follow the instructions on the screen to fill out the form and then click Register to complete the process." +
            " After successful Sign up, they can use their credential on the login page.    \n" +
            "That is it. Keep in mind that you will answer the question based on this information. If you can’t find the answer in this information, please kindly apologize to the user that you can’t find the information they need and tell them to contact Customer service at 816-273-3827 for further assistance. Let’s do this. I will give you a question for the user." +
            " You will act as a virtual assistant and answer the question. Here is the user question:\n";
    public ChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        if(OPENAI_API_KEY == null){
            return "No OpenAI api key found.";
        }else{
            try{
                String cleanedQuestion = (teach_model_about_the_web_app + question).replaceAll("[\\n\\t\\f\\r]", " ");
                //System.out.println(question);
                String requestBody = "{\"model\":\"gpt-3.5-turbo-0125\",\"messages\":[{\"role\":\"user\",\"content\":\"" + cleanedQuestion + "\"}]}";

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

}
