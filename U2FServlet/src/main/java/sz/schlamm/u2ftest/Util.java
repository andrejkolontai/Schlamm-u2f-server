package sz.schlamm.u2ftest;

import javax.json.Json;
import javax.json.JsonObject;

import sz.schlamm.u2f.messages.RegisteredKey;

/*
 * The infamous Util-class. Stuff nobody has a goot place for.
 * In this case, the conversion of registered keys into json
 */

public class Util {
	public static JsonObject key2Json(RegisteredKey k)  {
		return Json.createObjectBuilder().
				add("appId", k.getAppId()).
				add("version", k.getVersion()).
				add("keyHandle", k.getKeyHandle()).
				build();
		
	};
}
