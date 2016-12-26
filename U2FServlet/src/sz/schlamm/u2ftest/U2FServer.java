package sz.schlamm.u2ftest;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import sz.schlamm.u2f.KeyData;
import sz.schlamm.u2f.LoginState;
import sz.schlamm.u2f.RegistrationState;
import sz.schlamm.u2f.Server;
import sz.schlamm.u2f.U2FException;

/*This is the bridge between the servlet world and the schlamm u2f api
 * 
 * It's a ServletContextListener that runs every time the application
 * starts or stops.
 * 
 * It creates a Server object which is stateless. So it's safe to 
 * access it from multiple threads. 
 * 
 * In a DI-capable environment you could simply use a singleton 
 * (Like @ApplicationScoped or @Singleton).
 * 
 * */
@WebListener
public class U2FServer implements ServletContextListener{

	private static Server server;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		server = null; //cleaning up
	}

	/*The application starts up. Here is where the u2f api is initialized*/
	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();
		try {
			server = new Server(
						new SecureRandom(), /*let's just hope it's good enough*/ 
						"https://localhost:8443" /*Application ID, *must* be https...*/
					).
					addOrigin("https://localhost:8443"). /*add as many of them as you like*/
					addAttestationCA(
						/*every u2f device has a certificate from it's manufacturer. You can check whether it's genuine*/
						X509Certificate.getInstance(ctx.getResourceAsStream("WEB-INF/yubikey.pem")) 
					); 
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
	}

	public static Server getServer() {
		return Optional.ofNullable(server).orElseThrow(RuntimeException::new);
	}

	public static RegistrationState startRegistration(Collection<KeyData> userKeys) {
		return getServer().startRegistration(userKeys);
	}

	public static KeyData finishRegistration(RegistrationState registrationState) throws U2FException {
		return getServer().finishRegistration(registrationState);
	}

	public static LoginState startLogin(Collection<KeyData> userKeys) {
		return getServer().startLogin(userKeys);
	}

	public static KeyData finishLogin(LoginState loginState,
			Collection<KeyData> userKeys) throws U2FException {
		return getServer().finishLogin(loginState, userKeys);
	}
	
	
	
}
