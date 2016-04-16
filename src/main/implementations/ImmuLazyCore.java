package implementations;

import core.color.ImmuLazyColor;

/**
 * Created by maxus on 15.04.16.
 */
public class ImmuLazyCore extends Core {

	public ImmuLazyCore(int size) {
		super(size);
	}

	@Override
	public ImmuLazyColor<Integer> makeColor() {
		return new ImmuLazyColor<>(fields::getAdjacency);
	}

}