package server;

import core.views.State;

/**
 * Created by maxus on 03.04.16.
 */
public interface ExtView {
	void put(int x, int y);

	void pass();

	void undo();

	State getview();
}
