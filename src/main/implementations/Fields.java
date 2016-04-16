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
	private final HashBasedTable<Integer, Integer, Integer> fieldsCache = HashBasedTable.create();

	public final List<Integer> fields;
	public final List<Integer> xValues;
	public final List<Integer> yValues;
	public final List<List<Integer>> adjacents;

	private void init(List<Integer> fields, List<Integer> xvals, List<Integer> yvals, List<List<Integer>> adjacents) {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {

				fields.add(_field(x, y));
				xvals.add(x);
				yvals.add(y);
				fieldsCache.put(x, y, _field(x, y));

				ArrayList<Integer> adjs = new ArrayList<>();
				for(int l=0; l<4; l++) {
					int xa = x+xdifs[l];
					int ya = y+ydifs[l];

					if (0 <= xa && xa < size && 0 <= ya && ya < size)
						adjs.add(_field(xa, ya));
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
		this.xValues = Collections.unmodifiableList(xtemp);
		this.yValues = Collections.unmodifiableList(ytemp);
		this.adjacents = Collections.unmodifiableList(adjtemp);

	}

	public List<Integer> getAdjacency(Integer field) {
		return adjacents.get(field);
	}
	private Integer _field(int x, int y) {
		return x*size+y;
	}
	public Integer getField(int x, int y) {
		return fieldsCache.get(x, y);
	}
	public int getX(Integer f) {
		return xValues.get(f);
	}
	public int getY(Integer f) {
		return yValues.get(f);
	}
}
