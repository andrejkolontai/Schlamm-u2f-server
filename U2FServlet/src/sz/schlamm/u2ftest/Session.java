package sz.schlamm.u2ftest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import sz.schlamm.u2f.KeyData;
import sz.schlamm.u2f.LoginState;
import sz.schlamm.u2f.RegistrationState;

/*
 * We store everything inside the users's session. Of course, you would
 * want to store the user data into some persistent storage like a
 * file, directory service or database
 * 
 * In DI-capable environments like spring or CDI the session object
 * would always be around for injection. 
 * 
 * Here, in a plain servlet environment, I use a ThreadLocal variable
 * which is set to the session object on every request. Of course, this
 * only makes sense if every request is handled by his very own thread.
 * 
 * But this is not for production anyway. 
 */

@WebListener /*In modern servlet containers this makes it start up without web.xml config*/
public class Session implements ServletRequestListener {
	
	static final String ATTR_REG_STATE = "schlamm.u2f.registrationState";
	static final String ATTR_LOGIN_STATE = "schlamm.u2f.loginState";
	static final String ATTR_KEYDATA = "schlamm.u2f.keyData";
	
	/*Here is, where the session object is made available*/
	static ThreadLocal<HttpSession> mySession=new ThreadLocal<HttpSession>();

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		mySession.set(null); //just to make sure there are no references left.
	}

	@Override
	public void requestInitialized(ServletRequestEvent evt) {
		/*On the beginning of every request, we make the session object available through our
		 * ThreadLocal variable*/
		mySession.set(((HttpServletRequest)evt.getServletRequest()).getSession());
	}
	
	/*Some methods to access the session's content
	 * 
	 * The registration and login processes have a state. We need to save this
	 * state accross requests.
	 * */
	public static RegistrationState getRegistrationState() {
		return (RegistrationState) mySession.get().getAttribute(ATTR_REG_STATE);
	}
	
	public static void setRegistrationState(RegistrationState registrationState) {
		mySession.get().setAttribute(ATTR_REG_STATE, registrationState);
	}
	
	public static LoginState getLoginState() {
		return (LoginState) mySession.get().getAttribute(ATTR_LOGIN_STATE);
	}
	
	public static void setLoginState(LoginState loginState) {
		mySession.get().setAttribute(ATTR_LOGIN_STATE, loginState);
	}
	
	/*The user's registered keys. You should store them in some kind
	 * of persistent storage*/
	public static List<KeyData> getKeyData(){
		@SuppressWarnings("unchecked")
		List<KeyData> ret = (List<KeyData>) mySession.get().getAttribute(ATTR_KEYDATA);
		if (ret==null) {
			ret = new ArrayList<KeyData>();
			mySession.get().setAttribute(ATTR_KEYDATA, ret);
		}
		return ret;
	}
	
	public static void addKeyData(KeyData key){
		getKeyData().add(key);
	}
	
}
