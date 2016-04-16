package core;

/**
 * Created by maxus on 24.02.16.
 */
public enum Stone {
	WHITE(1), BLACK(-1), EMPTY(0);
	public final int val;
	private Stone opposite;

	static {
		WHITE.opposite = BLACK;
		BLACK.opposite = WHITE;
		EMPTY.opposite = EMPTY;
	}


	Stone(int i) {
		this.val = i;
	}

	public Stone opposite() {
		return opposite;
	}
}
