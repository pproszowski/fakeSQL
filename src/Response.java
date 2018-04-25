public class Response {
    private boolean isValid;
    private String message;

    public Response(){
        isValid = true;
        message = "";
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
