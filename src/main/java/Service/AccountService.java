package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    // import the DAO layer into the class block
    private AccountDAO accountDAO;

    /**
     * No-args constructor that creates a default AccountDAO
     * 
     */
    public AccountService() {
        accountDAO = new AccountDAO();
    }

    /**
     * Constructor that accepts the AccountDAO for testing purposes
     * @param accountDAO the DAO to use
     */
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Registers a new user account
     * @param account - the account to register (without account_id)
     * @return the registered account with its new account_id, or null if registration failed
     */
    public Account registerAccount(Account account) {
        // Validate the username (not blank)
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return null;
        }
        //password is at least 4 characters
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }

        // Check if the username already exists
        Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());
        if (existingAccount != null) {
            return null;
        }

        // Create the account
        return accountDAO.createAccount(account);
    }

    /**
     * user login
     * @param : account the account with username and password to check
     * @return the full account if login successful, null otherwise
     */
    public Account login(Account account) {
        // get the account from the db by username
        Account dbAccount = accountDAO.getAccountByUsername(account.getUsername());

        // check if the account exists and if the password provided by user matches the password in the db
        if (dbAccount != null && dbAccount.getPassword().equals(account.getPassword())) {
            return dbAccount;
        }

        return null;
    }

    /**
     * gets an account by its ID
     * @param account_id the account id
     * @return the account if found, null otherwise
     */
    public Account getAccountById(int account_id) {
        return accountDAO.getAccountById(account_id);
    }

    
}
