package Controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * [] /register
 * []
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
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        //username is not blank
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            context.status(400);
            return;
        }
        //password is at least 4 characters
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            context.status(400);
            return;
        }
        // and an Account with that username does not already exist
        // for now we have to assume that we have a wa to check if a username exists
        boolean usernameExists = false; // Placeholder until we get the Service Layer complete
        if (usernameExists) {
            context.status(400);
            return;
        }
        // for now we assume we can create and persist the account
        // this will be completed once the service later is complete
        Account createdAccount = account; /// another placeholder until we create the service later - then we will create and set the account_id

        // return the created account
        context.status(200).json(createdAccount);
    }

    private void loginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        // username match & password check 
        // TODO: add verification once service layer is implements
        // This will eventually check if the username and passowrd match an account int he databse
        boolean loginSuccessful = false; // placeholder - this will be replaced with actual verification
        Account existingAccount = null; // placeholder - this will be the account from the database which would provide  account_id, username, password

        // one we implement service layers and the login is successful we would be able to provide the json data upon successful login
        if (loginSuccessful) {
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

        // check if message text is blank (null or empty) - if blank return unsuccessful 400 (Client Error)
        if (message.getMessage_text() == null || message.message_text.trim().isEmpty()) {
            context.status(400);
            return;
        }
        // less than or equal to 255 char
        if (message.message_text.length() <= 255) {
            context.status(400);
            return;
        }
        // posted_by refers to a real, exisiting user - TODO: once DAO/service layer implemented
        boolean validUser = false; // placeholder to be replaced with actual validation when able

        // of the posted_by user doesnt exist
        if (!validUser) {
            context.status(400);
            return;
        }

        // createMessage object to create and retrieve the message_id of the message to return in the json body
        // 200  success message  and json of message including message_id 
        Message createdMessage = message;

        // Return the created message
        context.status(200).json(createdMessage);
    }
    
    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        // List<Message> messages object to hold
        List<Message> messages = messageService.getAllMessages(); // TODO: implement messageService layer
        //return all messagess
        context.status(200).json(messages);
    }
    
    private void getMessageByIdHandler(Context context) throws JsonProcessingException {
        // Extract message_id from path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));

        // get the message from service layer -- TODO: implement service later / DAO
        Message message = messageService.getMessageById(messageId);

        // return the message with status 200
        context.status(200).json(message);
        // Note: If message is null, Javalin will return an empty body with 200 status
    }

    private void deleteMessageByIdHandler(Context context) throws JsonProcessingException {
        // Extract message_id from path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));

        Message deletedMessage = messageService.deleteMessage(messageId); // TODO: implemenet service and DAO layers

        // Javalin will return with a status code of 200 always, 
        // if the message doesnt exist Javalin will return an empty body
        context.status(200).json(deletedMessage);
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
        Message updatedMessage = messageService.updatedMessageText(messageId, newMessageText); // TODO: create DAO/service layer

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
        List<Message> userMessages = messageService.getMessagesByUser(accountId); // TODO : implement messageServe

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