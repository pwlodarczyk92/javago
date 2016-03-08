package server;

import com.google.gson.Gson;
import game.GameExtView;
import game.standard.StandardBoard;
import spark.Request;
import spark.Response;

import java.util.HashMap;

/**
 * Created by maxus on 08.03.16.
 */
public class RestAPI {

	private HashMap<String, GameExtView> games = new HashMap<>();
	private Gson parser = new Gson();
	private class Position {
		public int x;
		public int y;
	}

	public String get(Request req, Response res) {

		String id = req.queryParams("id");

		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "";
		}

		GameExtView game = games.get(id);
		return parser.toJson(game.state());

	}

	public String put(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || games.containsKey(id)) {
			res.status(id == null ? 404 : 405);
			return "";
		}

		games.put(id, new StandardBoard());
		return "";

	}

	public String delete(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "";
		}

		games.remove(id);
		return "";

	}

	public String post(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "";
		}

		GameExtView game = games.get(id);
		String posstr = req.queryParams("position");
		if (posstr == null) {
			game.pass();
		}
		else {
			Position pos = parser.fromJson(posstr, Position.class);
			game.put(pos.x, pos.y);
		}
		return "";

	}
}
