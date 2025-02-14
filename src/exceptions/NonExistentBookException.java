package exceptions;

public class NonExistentBookException extends Exception{
    public NonExistentBookException(String message){
        super(message);
    }
}
