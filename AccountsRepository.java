

public interface AccountsRepository {
    void load();
    void save();

    boolean exists(String accountId);
    Account get(String accountId);

    void add(Account account);
    void remove(String accountId);

    String nextAccountId();

}

