package implementations;
import core.color.LazyColor;

/**
 * Created by maxus on 15.04.16.
 */
public class LazyCore extends Core {

	public LazyCore(int size) {
		super(size);
	}

	@Override
	public LazyColor<Integer> makeColor() {
		return new LazyColor<>(fields::getAdjacency);
	}

}