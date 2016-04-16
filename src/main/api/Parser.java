package api;

import core.board.IState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by maxus on 03.04.16.
 */
public class Parser<F> {

	public final BiFunction<Integer, Integer, F> fields;
	public final int size;

	public Parser(BiFunction<Integer, Integer, F> fields, int size) {
		this.fields = fields;
		this.size = size;
	}

	public <T> List<List<T>> toList(Map<F, T> data) {
		return toList(data::get);
	}

	public <T> List<List<T>> toList(Function<F, T> function) {
		List<List<T>> result = new ArrayList<>();
		for(int i=0; i< size; i++) {
			ArrayList<T> column = new ArrayList<>();
			for(int j=0; j< size; j++) {
				column.add(function.apply(fields.apply(i, j)));
			}
			result.add(column);
		}
		return result;
	}

	public void update(Snap result, IState<F, ?> view) {
		result.passcounter = view.getPassCount();
		result.currentstone = view.getCurrentStone().val;
		result.blackpoints = view.getBlackPoints();
		result.whitepoints = view.getWhitePoints();
		result.stones = toList(f -> view.getTable().getStone(f).val);
	}
}
