package core.table;

import core.color.ColorView;
import core.primitives.Stone;

import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface TableView<F, G> {

	ColorView<F, G> getview(Stone s);
	Stone getstone(F field);
	Set<F> getlibs(Stone s, G group);

}
