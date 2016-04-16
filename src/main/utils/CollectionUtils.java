package utils;

import java.util.Collections;
import java.util.Map;

/**
 * Created by maxus on 12.04.16.
 */
public class CollectionUtils {
	public static void normalize(Map<?, Double> map, Double zero, boolean center) {
		helpnormalize(map, zero, center);
	}
	private static <F> void helpnormalize(Map<F, Double> map, Double zero, boolean center) {

		if (map.isEmpty()) return;
		Double minscore = Collections.min(map.values());
		Double maxscore = Collections.max(map.values());
		if (zero != null) minscore -= zero;
		if (zero != null) maxscore -= zero;
		Double absscore = Math.max(Math.abs(minscore), Math.abs(maxscore));

		for(F field: map.keySet()) {
			Double score = map.get(field);
			if (zero != null) score -= zero;
			if (maxscore > minscore && center) {
				score -= minscore;
				score /= maxscore - minscore;
				score = score * 2.0 - 1.0;
			} else if (maxscore > minscore && !center) {
				score /= absscore;
			}
			map.put(field, score);
		}

	}
}
