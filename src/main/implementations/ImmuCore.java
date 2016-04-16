package implementations;

import core.color.ImmuColor;

/**
 * Created by maxus on 15.04.16.
 */
public class ImmuCore extends Core {

	public ImmuCore(int size) {
		super(size);
	}

	@Override
	public ImmuColor<Integer> makeColor() {
		return new ImmuColor<>(fields::getAdjacency);
	}

}