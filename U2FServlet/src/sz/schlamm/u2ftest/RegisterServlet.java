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

import sz.schlamm.u2f.KeyData;
import sz.schlamm.u2f.RegistrationState;
import sz.schlamm.u2f.U2FException;
import sz.schlamm.u2f.messages.RegistrationRequestMessage;
import sz.schlamm.u2f.messages.RegistrationResponseMessage;


/*
 * This servlet handles the registration process.
 * Usage is simple: a GET request gives you the RegistrationRequestMessage
 * 
 * You won't find any spec for this. The RegistrationRequestMessage holds simply
 * all information needed to talk to the u2f js api:
 * * challenge
 * * application id 
 * * the protocol version
 * * the user's keys that are known so far (to avoid registering the same key multiple times)
 * 
 * After the client page has successfully passed this data to the u2f client api it 
 * will hopefully receive a response from the token. This response
 * need to be sent to this servlet through a  POST request having the following parameters:
 * * registrationData
 * * clientData
 * * version
 * 
 * All of this data comes from the key. So you don't need to worry what's inside. 
 * It's described in "FIDO U2F Raw Message Formats".
 * 
 * */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*this starts the registration process by requesting a challenge from the u2f server*/
		RegistrationState regstate = U2FServer.startRegistration(Session.getKeyData());
		Session.setRegistrationState(regstate); // save that state into the session
		
		RegistrationRequestMessage req = regstate.getRequestMessage(); //that's the registration request...

		response.setContentType("application/json");
		
		/*... that needs to be converted into json*/
		response.getWriter().write(
			Json.createObjectBuilder().
				add("challenge", req.getChallenge()).
				add("version", req.getVersion()).
				add("appId", req.getAppId()).
				add(
					/*The user might already have registered this device. So we need to
					 * send all the user's key handles to the client so it can check
					 * whether this device is already registered*/
					"keys",
					req.getKeys().stream().map(Util::key2Json).
					collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
			).build().toString()
		);
		
		response.flushBuffer();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/*This handles the response from the client*/

		/*Check, if everything needed is here
		 * I don't care about checks here. registrationData and clientData
		 * are expeceted to be base64 encoded byte arrays. So they are passed
		 * to the base64 decoder. If it's something funny, it will not be
		 * able to decode it and throw an exception
		 * The server, of course, then checks if the byte array is in a proper format*/
		String registrationData = Objects.requireNonNull(request.getParameter("registrationData"));
		String clientData = Objects.requireNonNull(request.getParameter("clientData"));
		String version = Objects.requireNonNull(request.getParameter("version"));
		
		/*get the state we saved in the get-Request*/
		RegistrationState regstate = Session.getRegistrationState();
		/*and save the response*/
		regstate.setResponseMessage(new RegistrationResponseMessage(registrationData, clientData, version));
		try {
			/*now we can pass everything to the server and let it do all the work
			 * The result is, if everything is fine, the user's KeyData (keyHanlde+Public Key essentially)*/
			KeyData key = U2FServer.finishRegistration(regstate);
			/*Save the users key data. You should use some persistent storage*/
			Session.addKeyData(key);
			/*Redirect to a page reporting success*/
			response.sendRedirect("registrationFinished.xhtml");
		} catch (U2FException e) {
			/*If the registration fails for some reason, report that to the client
			 *What the server produces here is generally by no means js-parsable. 
			 *If you want proper error reporting, you should do something more like
			 *response.sendError(400 or 500,Jsonified error message)*/
			throw new ServletException(e);
		}
	}
}
