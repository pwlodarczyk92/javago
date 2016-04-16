package score;

/**
 * Created by maxus on 27.02.16.
 */
public class ScoreNotAllowed extends RuntimeException {
	public ScoreNotAllowed() {
	}

	public ScoreNotAllowed(String message) {
		super(message);
	}

	public ScoreNotAllowed(String message, Throwable cause) {
		super(message, cause);
	}

	public ScoreNotAllowed(Throwable cause) {
		super(cause);
	}

	public ScoreNotAllowed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
