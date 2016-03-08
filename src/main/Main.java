import com.google.gson.Gson;
import game.GameExtView;
import game.standard.StandardBoard;
import helpers.CGson.Check;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import server.Page;
import server.RestAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;

/**
 * Created by maxus on 20.02.16.
 */
public class Main {

	private RestAPI api = new RestAPI();

	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}

	public void run() {

		get("/game", api::get);
		put("/game", api::put);
		post("/game", api::post);
		delete("/game", api::delete);

	}

}
