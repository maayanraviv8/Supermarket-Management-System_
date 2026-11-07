package Supplier.Backend.serviceLayer;

public class Message<T> {
    private boolean success;
    private String message;
    private T data;


    public Message(boolean success, String message) {
        this.success = success;
        this.message = message;
        data = null;
    }
    public Message(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }

    public String toString(){
        if(success){
            if(data != null){
                return message + data.toString();
            }
            return message;
        }
        else {
            if(data != null){
                return "Error: " + message + data.toString();
            }
            return "Error: " + message;
        }
    }





}
