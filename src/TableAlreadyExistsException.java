public class TableAlreadyExistsException extends Exception {
    @Override
    public String getMessage() {
        return "Table already exists!";
    }
}
