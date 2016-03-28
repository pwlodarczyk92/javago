package core.table;

import core.color.ColorView;
import core.color.IColor;
import core.primitives.Stone;
import utils.Copyable;

import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface ITable<F, G, V extends TableView<F, G>> extends TableView<F, G> {

	public void put(Stone stone, F field);
	public V getview();

}
