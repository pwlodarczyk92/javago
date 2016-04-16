package api;

import java.util.List;
import java.util.Objects;

/**
 * Created by maxus on 03.04.16.
 */
public class Snap {
	public Integer whitepoints;
	public Integer blackpoints;
	public Integer passcounter;
	public Integer currentstone;
	public List<List<Integer>> stones;
	public List<List<Double>> scores;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Snap)) return false;

		Snap snap = (Snap) o;

		if (!whitepoints.equals(snap.whitepoints)) return false;
		if (!blackpoints.equals(snap.blackpoints)) return false;
		if (!passcounter.equals(snap.passcounter)) return false;
		if (!currentstone.equals(snap.currentstone)) return false;
		if (!stones.equals(snap.stones)) return false;
		if (!Objects.equals(scores, snap.scores)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = whitepoints.hashCode();
		result = 31 * result + blackpoints.hashCode();
		result = 31 * result + passcounter.hashCode();
		result = 31 * result + currentstone.hashCode();
		result = 31 * result + stones.hashCode();
		if (scores != null) result = 31 * result + scores.hashCode();
		return result;
	}
}
