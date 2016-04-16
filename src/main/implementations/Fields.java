package implementations;

import com.google.common.collect.HashBasedTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by maxus on 15.04.16.
 */
public class Fields {

	public final int size;
	private final HashBasedTable<Integer, Integer, Integer> fieldscache = HashBasedTable.create();

	public final List<Integer> fields;
	public final List<Integer> xvals;
	public final List<Integer> yvals;
	public final List<List<Integer>> adjacents;

	private void init(List<Integer> fields, List<Integer> xvals, List<Integer> yvals, List<List<Integer>> adjacents) {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {

				fields.add(calcfield(x, y));
				xvals.add(x);
				yvals.add(y);
				fieldscache.put(x, y, calcfield(x, y));

				ArrayList<Integer> adjs = new ArrayList<>();
				for(int l=0; l<4; l++) {
					int xa = x+xdifs[l];
					int ya = y+ydifs[l];

					if (0 <= xa && xa < size && 0 <= ya && ya < size)
						adjs.add(calcfield(xa, ya));
				}
				adjacents.add(Collections.unmodifiableList(adjs));
			}
		}
	}

	public Fields(int size) {
		this.size = size;

		List<Integer> ftemp = new ArrayList<>();
		List<Integer> xtemp = new ArrayList<>();
		List<Integer> ytemp = new ArrayList<>();
		List<List<Integer>> adjtemp = new ArrayList<>();
		init(ftemp, xtemp, ytemp, adjtemp);
		this.fields = Collections.unmodifiableList(ftemp);
		this.xvals = Collections.unmodifiableList(xtemp);
		this.yvals = Collections.unmodifiableList(ytemp);
		this.adjacents = Collections.unmodifiableList(adjtemp);

	}

	public List<Integer> adjacent(Integer field) {
		return adjacents.get(field);
	}
	private Integer calcfield(int x, int y) {
		return x*size+y;
	}

	public Integer field(int x, int y) {
		return fieldscache.get(x, y);
	}
	public int x(Integer f) {
		return xvals.get(f);
	}
	public int y(Integer f) {
		return yvals.get(f);
	}
}
