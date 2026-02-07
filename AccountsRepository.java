/*
 * AccountsRepository.java
 - Defines the contract for storing, retrieving, and managing Account objects.
 - Implementations of this interface are responsible for handling
   loading from and saving to a data source, and providing basic
   account operations.
 */

public interface AccountsRepository {
    /*
     * Loads account data from the underlying data source into memory
     * This method should be called before any account operations are performed
     */
    void load();

    /*
    * Saves all in-memory account data to the underlying data source
    * This method should be called after account modifications to persist changes
    */
    void save();

    /**
     * Checks whether an account with the specified ID exists
     * @param accountId Unique account ID to look up
     * @return true if the account exists, false otherwise
     */
    boolean exists(String accountId);

    /**
     * Retrieves an account by its unique ID
     * @param accountId Unique account ID
     * @return the Account associated with the given ID, or null if not found
     */
    Account get(String accountId);

    /**
     * Adds a new account to the repository.
     * @param account Account object to be stored
     */
    void add(Account account);

    /**
     * Removes an account from the repository using its unique ID.
     * @param accountId Unique account ID of the account to remove
     */
    void remove(String accountId);

    /**
     * Generates the next available unique account ID.
     * @return a new 5-digit account ID as a String
     */
    String nextAccountId();
}

