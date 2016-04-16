package api;

import score.Score;

/**
 * Created by maxus on 03.04.16.
 */
public interface ExtView {
	public void put(int x, int y);

	public void pass();

	public void undo();

	public Snap getview();

	public Snap getview(Score score);

}
