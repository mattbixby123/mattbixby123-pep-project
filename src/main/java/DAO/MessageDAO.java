package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    
    /**
     * Retrieves all messages from the database
     * @return a list of all messages
     */
    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                Message message = new Message (
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    /**
     * retrieves a message by its ID
     * @param message_id the message ID to search for
     * @return the Message if found, null otherwise
     */

     public Message getMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Message (
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                    );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
     }

     /**
      * creates a new message in the db
      * @param message, the message to create (excluding message_id)
      * @return the newly created messsage  with its new message_id, or null if creation failed
      */
     public Message createMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return new Message (
                        generatedId,
                        message.getPosted_by(),
                        message.getMessage_text(),
                        message.getTime_posted_epoch()
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
     }

     /**
      * deletes a message from the db
      * @param message_id the Id of the message to delete
      * @return the deleted message, or null if the message didnt exist
      */
      public Message deleteMessage(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            // first we need to get the message 
            Message message = getMessageById(message_id);
            if (message == null) {
                return null;
            }

            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            // Use executeUpdate for DELETE statements, not executeQuery
            int rowsAffected = preparedStatement.executeUpdate();
            
            // If the delete was successful, return the original message object
            if (rowsAffected > 0) {
                return message;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
      }

      /**
       * updates a message's text
       * @param message_id, the id of the message to update
       * @param newMessageText the new text for the message
       * @return the updated message, or null if update failed
       */
      public Message updateMessageText(int message_id, String newMessageText) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessageText);
            preparedStatement.setInt(2, message_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return getMessageById(message_id);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
      }

      /**
       *  retrieves all message posted by a specific user
       *  @param account_id the Account ID of the user
       *  @return a list of message posted by the user
       */
      public List<Message> getMessagesByUser(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
      }

}
