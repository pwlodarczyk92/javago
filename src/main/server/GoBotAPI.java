package server;

import bot.Score;
import spark.Request;
import spark.Response;
import standard.Implementation;

/**
 * Created by maxus on 31.03.16.
 */
public class GoBotAPI extends GoAPI<ScoredView>  {

	protected String put(Request req, Response res, String id) {
		games.put(id, Implementation.scoredView());
		res.status(201);
		return "{}";
	}


	@Override
	protected String get(Request request, Response response, ScoredView game) {
		String rawscore = request.queryParams("score");
		if (rawscore == null) {
			return super.get(request, response, game);
		}
		logger.info("GET: score argument specified: {}", rawscore);
		Score scoretype = Score.valueOf(rawscore);
		return parser.toJson(game.getview(scoretype));

	}

}
