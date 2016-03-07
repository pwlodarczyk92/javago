package game.standard;

import core.primitives.Stone;
import core.board.Board;
import core.table.Table;
import core.color.IntColor;
import game.GameExtView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maxus on 06.03.16.
 */
public class StandardBoard extends Board<Integer, Integer, IntColor> implements GameExtView {

	private static HashMap<Integer, List<Integer>> adjacents = new HashMap<>();
	static {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				ArrayList<Integer> adjs = new ArrayList<>();
				for(int l=0; l<4; l++) {
					int x = i+xdifs[l];
					int y = j+ydifs[l];
					if (0<=x && x<19 && 0<=y && y<19) {
						adjs.add(field(x, y));
					}
				}
				adjacents.put(field(i, j), adjs);
			}
		}
	}

	public StandardBoard() {
		super(new Table<>(adjacents::get, new IntColor(adjacents::get), new IntColor(adjacents::get)));
	}

	private static Integer field(int x, int y) {
		return x*19+y;
	}

	public Stone get(int x, int y) {
		return getStone(field(x, y));
	}

	@Override
	public List<List<Stone>> get() {
		List<List<Stone>> columns = new ArrayList<>();
		for(int i=0; i<18; i++) {
			ArrayList<Stone> column = new ArrayList<>();
			for (int j = 0; j < 18; j++) {
				column.add(get(i, j));
			}
			columns.add(column);
		}
		return columns;
	}

	@Override
	public void put(int x, int y) {
		put(field(x, y));
	}

}
