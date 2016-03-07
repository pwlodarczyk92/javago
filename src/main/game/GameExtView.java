package game;

import core.primitives.Stone;

import java.util.List;

/**
 * Created by maxus on 07.03.16.
 */
public interface GameExtView {
	public void put(int x, int y);
	public List<List<Stone>> get();
}
