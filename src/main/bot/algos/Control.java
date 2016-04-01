package bot.algos;

import core.color.ColorView;
import core.primitives.Stone;
import core.table.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;


/**
 * Created by maxus on 30.03.16.
 */
public class Control {

	final private static Logger logger = LoggerFactory.getLogger(Control.class.getName());

	private static <F> Double getscore(HashMap<F, Double> result, F field, Function<F, Collection<F>> adjacency) {

		Double i = null;
		for (F adj: adjacency.apply(field)) {
			Double ni = result.get(adj);
			if (i == null)
				i = ni;
			else if(ni != null && !i.equals(ni))
				return 0.0;
		}
		return i == null ? 0.0 : i;

	}

	// returns map:
	// for each node closer than <rounds>+1 to a nearest stone
	// 1.0 if nearest stone is white, -1.0 if black,
	// 0.0 if both black and white are equidistant
	public static <F> HashMap<F, Double> get(TableView<F, ?> view, int rounds) {

		Function<F, Collection<F>> adjacency = view.getadjacency();
		HashMap<F, Double> result = new HashMap<>();
		ColorView<F, ?> wview = view.getview(Stone.WHITE);
		ColorView<F, ?> bview = view.getview(Stone.BLACK);

		for (F field: wview.allstones())
			result.put(field, +1.0);
		for (F field: bview.allstones())
			result.put(field, -1.0);

		Integer round = 0;
		HashMap<F, Double> frontresult = new HashMap<>(result);

		while(!frontresult.isEmpty()) {

			if (round == rounds)
				break;
			round +=1;

			HashMap<F, Double> newresult = new HashMap<>();

			for (F field: frontresult.keySet()) {
				for(F adj: adjacency.apply(field)) {
					if (!result.containsKey(adj) && !newresult.containsKey(adj))
						newresult.put(adj, getscore(frontresult, adj, adjacency));
				}
			}

			frontresult = newresult;
			result.putAll(newresult);

		}

		return result;

	}

	public static <F> HashMap<F, Double> get(TableView<F, ?> view) {

		return get(view, 3);

	}

}
