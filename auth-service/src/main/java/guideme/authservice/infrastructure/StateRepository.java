package guideme.authservice.infrastructure;

public interface StateRepository {
    String save(String state, String userId);

    String findById(String state);

    String delete(String state);
}
