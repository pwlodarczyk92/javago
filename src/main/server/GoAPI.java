package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.board.StandardBoard;
import core.primitives.MoveNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;

/**
 * Created by maxus on 08.03.16.
 */
public class GoAPI {

	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	private HashMap<String, GameExtView> games = new HashMap<>();
	private Gson parser = new Gson();
	private class Position {
		public int x;
		public int y;
	}

	//private void log(String info) {
	//	System.out.println(info);
	//}

	public String get(Request req, Response res) {

		logger.info("GET: {}", req.queryString());
		String id = req.queryParams("id");

		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		GameExtView game = games.get(id);
		return parser.toJson(game.state());

	}

	public String put(Request req, Response res) {

		logger.info("PUT: {}", req.queryString());
		String id = req.queryParams("id");
		if (id == null || games.containsKey(id)) {
			res.status(id == null ? 404 : 200);
			return "{}";
		}

		games.put(id, new StandardBoard());
		res.status(201);
		return "{}";

	}

	public String delete(Request req, Response res) {

		logger.info("DELETE: {}", req.queryString());
		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		games.remove(id);
		return "{}";

	}

	public String post(Request req, Response res) {

		String body = req.body();
		logger.info("POST: {}", req.queryString());
		logger.info("body: {}", body);

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		GameExtView game = games.get(id);
		JsonObject jbody = parser.fromJson(body, JsonObject.class);
		String action = parser.fromJson(jbody.get("action"), String.class);
		try {
			switch (action) {
				case "pass":
					game.pass();
					break;
				case "undo":
					game.undo();
					break;
				case "move":
					Position pos = parser.fromJson(jbody.get("position"), Position.class);
					game.put(pos.x, pos.y);
					break;
				default:
					res.status(404);
			}
		} catch (MoveNotAllowed e) {
			res.status(400);
		}
		return "{}";
	}
}
