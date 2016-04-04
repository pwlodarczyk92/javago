package server;

import bot.Board.BottedBoard;
import bot.Score;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

/**
 * Created by maxus on 31.03.16.
 */
public class GoBotAPI extends GoAPI<BottedBoard>  {

	public GoBotAPI() {
		super();
		actions.put("botmove", this::dobotmove);
	}

	private String dobotmove(Request request, Response response, BottedBoard bottedBoard, JsonObject jsonObject) {
		bottedBoard.dobotmove();
		return "{}";
	}

	protected String put(Request req, Response res, String id) {
		games.put(id, new BottedBoard());
		res.status(201);
		return "{}";
	}

	@Override
	protected String get(Request request, Response response, BottedBoard game) {
		String rawscore = request.queryParams("score");
		if (rawscore == null) {
			return super.get(request, response, game);
		}
		logger.info("GET: score argument specified: {}", rawscore);
		Score scoretype = Score.valueOf(rawscore);
		return parser.toJson(game.state(scoretype));

	}

}
