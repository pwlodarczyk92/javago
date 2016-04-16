package implementations;
import core.color.Color;

/**
 * Created by maxus on 15.04.16.
 */
public class StandardCore extends Core {

	public StandardCore(int size) {
		super(size);
	}

	@Override
	public Color<Integer> makeColor() {
		return new Color<>(fields::getAdjacency);
	}

}
