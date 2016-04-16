import api.GoAPI;
import implementations.ImmuLazyCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

/**
 * Created by maxus on 20.02.16.
 */
public class Server {

	//--singleton--
	private static Server server = null;
	private Server() {}
	public static Server getServer() {
		if (server == null)
			server = new Server();
		return server;
	}
	//--singleton

	public static class Config {
		public int port = 8123;
		public boolean cors = false;
		public String path = "/game";
	}

	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	private GoAPI api = new ImmuLazyCore(19).makeGameApi();
	private Config config = new Config();
	private boolean running = false;

	public void configure(Server.Config config) {
		if (running) throw new RuntimeException();
		this.config = config;
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Allow-Methods", methods);
			response.header("Access-Control-Allow-Headers", headers);
		});
	}

	public void run() {

		if (running) throw new RuntimeException();
		else running = true;

		port(config.port);
		if (!config.cors) enableCORS("*", "GET, POST, PUT, DELETE", "*");
		before((req, res) -> logger.info("spark {} request started: {}", req.requestMethod(), req.queryString()));
		before((req, res) -> { if (!req.body().equals("")) logger.info("request body: {}", req.body()); });
		after((req, res) -> logger.info("spark {} request finished: {}", req.requestMethod(), req.queryString()));
		get(config.path, api::get);
		put(config.path, api::put);
		post(config.path, api::post);
		delete(config.path, api::delete);
		options(config.path, (req, res) -> "{}");

	}

}
