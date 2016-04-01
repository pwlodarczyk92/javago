package server;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maxus on 31.03.16.
 */
public interface ScoredView<F, S extends Enum<S>> extends GameExtView<F> {

	public static class ScoreState extends State {
		public List<List<Double>> scores;
	}

	public Map<F, Double> getscores(S score);
	public void dobotmove();

	public default ScoreState state(S score) {

		ScoreState result = new ScoreState();
		Map<F, Double> scores = getscores(score);
		updateState(result);
		result.scores = getfields().stream().map(
				column -> column.stream().map(scores::get).collect(Collectors.toList())
		).collect(Collectors.toList());
		return result;

	}
}
