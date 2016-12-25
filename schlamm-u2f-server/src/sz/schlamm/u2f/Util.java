package sz.schlamm.u2f;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

public class Util {
	public static String toHex(byte[] array){
		return Optional.ofNullable(array).map(a -> new BigInteger(a).toString(16).toUpperCase()).orElse(null);
	}
	
	public static String toB64(byte[] array) {
		return Optional.ofNullable(array).map(a -> Base64.getUrlEncoder().withoutPadding().encodeToString(a)).orElse(null);
	}
	
	public static byte[] fromB64(String b64){
		return Optional.ofNullable(b64).map(s -> Base64.getUrlDecoder().decode(s)).orElse(null);
	}
	
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
}
