package core;

/**
 * Created by maxus on 27.02.16.
 */
public class MoveNotAllowed extends RuntimeException {
	public MoveNotAllowed() {
	}

	public MoveNotAllowed(String message) {
		super(message);
	}

	public MoveNotAllowed(String message, Throwable cause) {
		super(message, cause);
	}

	public MoveNotAllowed(Throwable cause) {
		super(cause);
	}

	public MoveNotAllowed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
