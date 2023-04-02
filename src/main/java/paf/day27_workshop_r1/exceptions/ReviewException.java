package paf.day27_workshop_r1.exceptions;

public class ReviewException extends RuntimeException {

    public ReviewException() {
        super();
    }

    public ReviewException(String message) {
        super(message);
    }

    public ReviewException(String message, Throwable cause) {
        super(message, cause);
    }

}
