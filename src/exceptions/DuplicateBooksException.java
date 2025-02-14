package exceptions;

public class DuplicateBooksException extends Exception {
    public DuplicateBooksException(String message){
        super(message);
    }
}
