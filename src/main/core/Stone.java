package core;

/**
 * Created by maxus on 24.02.16.
 */
public enum Stone {
	WHITE, BLACK, EMPTY;
	private Stone opposite;

	static {
		WHITE.opposite = BLACK;
		BLACK.opposite = WHITE;
		EMPTY.opposite = EMPTY;
	}

	public Stone opposite() {
		return opposite;
	}
}
