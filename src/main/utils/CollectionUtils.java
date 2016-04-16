package utils;

import java.util.Collections;
import java.util.Map;

/**
 * Created by maxus on 12.04.16.
 */
public class CollectionUtils {
	public static void normalize(Map<?, Double> map, Double zero, boolean center) {
		_normalize(map, zero, center);
	}
	private static <F> void _normalize(Map<F, Double> map, Double zero, boolean center) {

		if (map.isEmpty()) return;
		Double minScore = Collections.min(map.values());
		Double maxScore = Collections.max(map.values());
		if (zero != null) minScore -= zero;
		if (zero != null) maxScore -= zero;
		Double maxAbsScore = Math.max(Math.abs(minScore), Math.abs(maxScore));

		for(F field: map.keySet()) {
			Double score = map.get(field);
			if (zero != null) score -= zero;
			if (maxScore > minScore && center) {
				score -= minScore;
				score /= maxScore - minScore;
				score = score * 2.0 - 1.0;
			} else if (maxScore > minScore && !center) {
				score /= maxAbsScore;
			}
			map.put(field, score);
		}

	}
}
