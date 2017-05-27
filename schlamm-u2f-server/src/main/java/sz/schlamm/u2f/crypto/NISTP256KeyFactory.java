package sz.schlamm.u2f.crypto;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;


/**
 * This class is used to convert the public key data into a java.security.PublicKey
 * we can check the signatures with. 
 * 
 * Unfortunately, there is no "factory" like method in java where we just say
 * that's a NIST p-256 Public Key, stick the bytes in and get a PublicKey 
 * object out. But, with a little research, we can do this ourselves.
 *
 */
public class NISTP256KeyFactory {
	
	/*NIST P-256 Parameters
	 * "RECOMMENDED ELLIPTIC CURVES FOR FEDERAL GOVERNMENT USE"
	 * https://www.researchgate.net/file.PostFileLoader.html?id=57874e34217e205d2575acb9&assetKey=AS:383668775342080@1468485172273
	 * 
	 * http://csrc.nist.gov/groups/ST/ecc-workshop-2015/papers/session6-adalier-mehmet.pdf
	 * 
	 * y^2 = x^3 + ax + b mod q 
	 * */
	
	//q
	private static final ECFieldFp field = new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951"));
	//a (a = q-3)
	private static final BigInteger a = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948");
	
	//b
	private static final BigInteger b = new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291");
	
	private static final EllipticCurve curve = new EllipticCurve(field, a, b);
	
	// Generator/Base Point
	private static final ECPoint generator = new ECPoint(
    	new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
    	new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")
	);
	
	private static final BigInteger order = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");
	
	private static final ECParameterSpec nistP256spec = new ECParameterSpec(curve, generator, order, 1);

	/**
	 * This method takes the public key bytes we have saved for the user in the 
	 * registration process and returns a PublicKey object we can check the 
	 * signature with
	 * 
	 * @param encodedKey the public key bytes
	 * @return the PublicKey object
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey decodeKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException{
		if (encodedKey.length!=65) {
			throw new IllegalArgumentException("This key might be correct. But I don't know how to handle it, it does not have 65 bytes");
		}
		if (encodedKey[0] != 4) {
			throw new IllegalArgumentException("There should be the a \"4\" in the first byte, see http://stackoverflow.com/questions/6665353/public-key-length");
		}
		
		ECPoint point = new ECPoint(
			new BigInteger(Arrays.copyOfRange(encodedKey, 1, 32+1)),
			new BigInteger(Arrays.copyOfRange(encodedKey, 32+1, 32+32+1))
		);
		
		KeyFactory kfa = KeyFactory.getInstance ("EC");
		
		ECPublicKeySpec publicKeySpec = new ECPublicKeySpec (point, nistP256spec);
		return kfa.generatePublic(publicKeySpec);
	}
}
