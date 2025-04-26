package Service;

import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    /**
     * no-args constructor that creaters default DAOs
     */
    public MessageService() {
        messageDAO = new MessageDAO();
        accountDAO = new AccountDAO();
    }

    /**
     * constructor that accepts DAOs for testing purposes
     * @param messageDAO the message DAO to use
     * @return accountDAO the account DAO to use
     */
    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    /**
     * gets all messages
     * @return a list of all messages
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    /**
     * gets a specific message by its message_id
     * @param message_id
     * @return the message if found, null otherwise
     */
    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    /**
     * creates a new message
     * @param message to create (without message_id)
     * @return the created message with its new message_id, or nul if creation failed
     */
    public Message createMessage(Message message) {
        // Validate message text (not blank, not over 255 chars)
        if (message.getMessage_text() == null ||
            message.getMessage_text().trim().isEmpty() ||
            message.getMessage_text().length() > 255) {
                return null;
            }

            // validate that posted_by referes to a real user
            Account account = accountDAO.getAccountById(message.getPosted_by());
            if (account == null) {
                return null;
            }

            // set the current time if not provided
            if (message.getTime_posted_epoch() == 0) {
                message = new Message(
                    message.getMessage_id(),
                    message.getPosted_by(),
                    message.getMessage_text(),
                    System.currentTimeMillis()
                );
            }

            // create the message
            return messageDAO.createMessage(message);
    }

    /**
     * deletes a message
     * @param message_id
     * @return the deleted message, null if the message didnt exist
     */
    public Message deleteMessage(int message_id) {
        return messageDAO.deleteMessage(message_id);
    }

    /**
     * updates a message's text
     * @param message_id
     * @param newMessageText 
     * @return the updated message, or null if update failed
     */
    public Message updateMessageText(int message_id, String newMessageText) {
        // validate the message exists
        Message exisitingMessage = messageDAO.getMessageById(message_id);
        if(exisitingMessage == null) {
            return null;
        }

        // validate message text (not blank, and not over 255 chars)
        if (newMessageText == null ||
            newMessageText.trim().isEmpty() ||
            newMessageText.length() > 255) {
                return null;
            }
        
        // update the message
        return messageDAO.updateMessageText(message_id, newMessageText);
    }

    /**
     * gets all messages posted by a specific user
     * @param account_id
     * @return a list of messages posted by the user
     */
    public List<Message> getMessagesByUser(int account_id) {
        return messageDAO.getMessagesByUser(account_id);
    }
}
