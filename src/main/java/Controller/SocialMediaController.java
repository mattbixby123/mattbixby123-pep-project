package Controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * DONE: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::messagesHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler); // get localhost:8080/messages/{message_id} #5
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);// delete localhost:8080/messages/{message_id} #6
        app.patch("/messages/{message_id}", this::patchMessageByIdHandler);// patch localhost:8080/messages/{message_id} #7
        app.get("/accounts/{account_id}/messages", this::getAllMessageByAccountIdHandler);// get localhost:8080/accounts/{account_id}/messages #8
        app.get("example-endpoint", this::exampleHandler);

        return app;
    }

    private void registerHandler(Context context) throws JsonProcessingException {
        // OM mapper object to map json to account body of account object
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        
        // use the AccountService layer to register the account
        AccountService accountService = new AccountService();
        Account createdAccount = accountService.registerAccount(account);

        // if account creation failed (already exists or else) return 400
        if (createdAccount == null) {
            context.status(400);
            return;
        }
        // return the created account
        context.status(200).json(createdAccount);
    }

    private void loginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        // username match & password check 
        // Half done: add verification once service layer is implemented
        AccountService accountService = new AccountService();
        Account existingAccount = accountService.login(account); 

        // one we implement service layers and the login is successful we would be able to provide the json data upon successful login
        if (existingAccount != null) {
            context.status(200).json(existingAccount);
        } else {
            context.status(401); // if login not successful we are asked to return (Unauthorized #401)
        }

        // future goals - implement  a Session token to allow the user to securily use the site/ authorize themselves
    }

    private void messagesHandler(Context context) throws JsonProcessingException {
        //ObjectMapper and Account object declarations
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        // use the MessageService to create a MS object and the message
        MessageService messageService = new MessageService();
        Message createdMessage = messageService.createMessage(message);
        
        // if message creation failed, return 400
        if (createdMessage == null) {
            context.status(400);
            return;
        }

        // Return the created message
        context.status(200).json(createdMessage);
    }
    
    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        MessageService messageService = new MessageService();
        // List<Message> messages object to hold
        List<Message> messages = messageService.getAllMessages(); 
        //return all messagess
        context.status(200).json(messages);
    }
    
    private void getMessageByIdHandler(Context context) throws JsonProcessingException {
        // Extract message_id from path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));

        // get the message from service layer
        MessageService messageService = new MessageService();
        Message message = messageService.getMessageById(messageId);

        // return the message with status 200 always
        context.status(200);

        // if there is a message, return it
        if (message != null) {
            context.json(message);
        }
        // Note: If message is null, Javalin will return an empty body with 200 status
    }

    private void deleteMessageByIdHandler(Context context) throws JsonProcessingException {
        // Extract message_id from path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));

        // Debug: Print message ID
        // System.out.println("Deleting message with ID: " + messageId);

        // call the service layer to delete the message
        MessageService messageService = new MessageService();
        Message deletedMessage = messageService.deleteMessage(messageId); 

        // Debug: Check if message was found
        // System.out.println("Deleted message: " + deletedMessage);

        // Javalin will return with a status code of 200 always, 
        // if the message doesnt exist Javalin will return an empty body
        if (deletedMessage != null) {
            // Debug: Print JSON
            // String json = new ObjectMapper().writeValueAsString(deletedMessage);
            // System.out.println("JSON to return: " + json);

            context.json(deletedMessage);
        } else {
            context.status(200);
        }
    }

    private void patchMessageByIdHandler(Context context) throws JsonProcessingException {
        // Extract message_id from path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));

        // use OM class to create an OM object  to directly map the JSON to a Map collection
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestMap = mapper.readValue(context.body(), new TypeReference<Map<String, String>>() {});

        // get the message_text from the map
        String newMessageText = requestMap.get("message_text");

        // call the service layer to update the message
        MessageService messageService = new MessageService();
        Message updatedMessage = messageService.updateMessageText(messageId, newMessageText); 

        // if the message update was successful (not null), return 200 with the updated message
        if (updatedMessage != null) {
            context.status(200).json(updatedMessage);
        } else {
            // if update fialed, return 400
            context.status(400);
        }

    }

    private void getAllMessageByAccountIdHandler(Context context) throws JsonProcessingException {
        // Extract account_id from path parameter
        int accountId = Integer.parseInt(context.pathParam("account_id"));

        // get all message from the user via the service layer
        MessageService messageService = new MessageService();
        List<Message> userMessages = messageService.getMessagesByUser(accountId); 

        // return the message in json with status 200
        context.status(200).json(userMessages);


    }


    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


}