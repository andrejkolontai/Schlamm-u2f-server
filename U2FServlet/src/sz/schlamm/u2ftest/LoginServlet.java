package sz.schlamm.u2ftest;

import java.io.IOException;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sz.schlamm.u2f.LoginState;
import sz.schlamm.u2f.U2FException;
import sz.schlamm.u2f.messages.SignRequestMessage;
import sz.schlamm.u2f.messages.SignResponseMessage;

/*
 * This is the LoginServlet that handles authentication. Just like the RegistrationServlet
 * it starts the process by requesting a challenge from the server and than waiting
 * for a POST request containing the response.
 * */

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /*Starts the login process. Returns a SignRequestMessage to the client. 
     * This SignRequestMessage contains:
     * * a challenge
     * * the protocol version
     * * application ID
     * * all the key handles that are known for the user
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (Session.getKeyData().isEmpty()) {
			/*if there are no keys, theres is no point doing this process*/
			response.sendError(400, "You don't have any keys registered yet");
			return;
		}
		/*Start the process by requesting a new state*/
		LoginState state = U2FServer.startLogin(Session.getKeyData());
		/*Save this state into the session*/
		Session.setLoginState(state);

		/*That's the message we need to pass to the client...*/
		SignRequestMessage msg = state.getSignRequestMessage();
		/*...in a jsonified form*/
		response.setContentType("application/json");
		response.getWriter().write(
			Json.createObjectBuilder().
				add("appId", msg.getAppId()).
				add("challenge", msg.getChallenge()).
				add("version", msg.getVersion()).
				add(
					"keys", 
					msg.getKeys().
						stream().
						map(Util::key2Json).
						collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
				).
				build().
				toString()
		);
		response.flushBuffer();
	}

	/*This handles the response from the server it receives three parameters that 
	 * are provided by the key. So you don't need to care what's inside them 
	 * They are specified in FIDO U2F Raw Message Formats*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*check if everything is there*/
		String signatureData = Objects.requireNonNull(request.getParameter("signatureData"));
		String clientData = Objects.requireNonNull(request.getParameter("clientData"));
		/*With the key handle we can identify the key and now which Public Key to use to
		 * check the signature*/
		String keyHandle = Objects.requireNonNull(request.getParameter("keyHandle")); 
		
		/*wrap it all up*/
		SignResponseMessage msg = new SignResponseMessage(signatureData,clientData,keyHandle);
		/*get the state saved in the get request. If it's not there, we can't continue*/
		LoginState state = Objects.requireNonNull(Session.getLoginState());
		
		/*Save the response to the state*/
		state.setSignResponseMessage(msg);
		try {
			/*no we can pass everything to the server to do 
			 * all the checks*/
			U2FServer.finishLogin(state, Session.getKeyData());
			/*if everything is fine, just redirect to some success page*/
			response.sendRedirect("loginSuccess.xhtml");
			/*The is, however, something missing. The key increments a counter so you can detect 
			 * a cloned device the server saves that counter in the user's key data. You'll 
			 * have to save it to the persistent key storage
			 * 
			 * RegisteredKey usedKey = U2FServer.finishLogin(state, Session.getKeyData());
			 * saveThatKeyToPersistentStorage(usedKey);
			 * 
			 * */
		} catch (U2FException e) {
			/*If the verification fails for some reason, report that to the client
			 *What the server produces here is generally by no means js-parsable. 
			 *If you want proper error reporting, you should do something more like
			 *response.sendError(400 or 500,Jsonified error message)*/
			throw new ServletException(e);
		}

	}

}
