package core.views;

import core.board.StateView;

import java.util.ArrayList;
import java.util.function.BiFunction;

/**
 * Created by maxus on 03.04.16.
 */
public class Parser<F> {

	public final BiFunction<Integer, Integer, F> fields;
	public static final int nteen = 19;

	public Parser(BiFunction<Integer, Integer, F> fields) {
		this.fields = fields;
	}

	public void update(State result, StateView<F, ?> view) {
		result.passcounter = view.getpasscount();
		result.currentstone = view.getcurrentstone().val;
		result.blackpoints = view.getblackpoints();
		result.whitepoints = view.getwhitepoints();

		result.stones = new ArrayList<>();
		for(int i=0; i< nteen; i++) {
			ArrayList<Integer> column = new ArrayList<>();
			for(int j=0; j< nteen; j++) {
				column.add(view.gettable().getstone(fields.apply(i, j)).val);
			}
			result.stones.add(column);
		}

	}
}
