import java.lang.reflect.*;
import java.util.*;

public class CheckRepo {
  public static void main(String[] args) throws Exception {
    FileAccountsRepository repo = new FileAccountsRepository(args[0]);
    repo.load();

    System.out.println("exists 01290 = " + repo.exists("01290"));
    System.out.println("exists 01256 = " + repo.exists("01256"));

    // reflectively read the private 'accounts' map
    Field f = FileAccountsRepository.class.getDeclaredField("accounts");
    f.setAccessible(true);
    Map<?,?> m = (Map<?,?>) f.get(repo);
    System.out.println("loaded count = " + m.size());
    System.out.println("first keys:");
    int k=0;
    for (Object key : m.keySet()) {
      System.out.println("  " + key);
      if (++k>=5) break;
    }
  }
}
