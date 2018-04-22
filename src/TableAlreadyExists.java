public class TableAlreadyExists extends Exception {
    @Override
    public String getMessage() {
        return "Table already exists!";
    }
}
