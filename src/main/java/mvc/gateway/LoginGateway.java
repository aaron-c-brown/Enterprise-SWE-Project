package mvc.gateway;

import mvc.model.HashUtils;
import mvc.model.User;
import mvc.screens.MainController;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;

public class LoginGateway {
    private static final Logger LOGGER = LogManager.getLogger();


    public static SessionGateway login(String userName, String password)
    {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost("http://localhost:8080/login");

            // use this for submitting form data as raw json
            JSONObject formData = new JSONObject();
            formData.put("login", userName);
            formData.put("password", password);
            String formDataString = formData.toString();

            // The login request won't work without these two lines
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");
            //

            StringEntity reqEntity = new StringEntity(formDataString);
            postRequest.setEntity(reqEntity);
            postRequest.setEntity(reqEntity);


            response = httpclient.execute(postRequest);

            switch(response.getStatusLine().getStatusCode()) {
                case 200:
                    HttpEntity entity = response.getEntity();
                    // use org.apache.http.util.EntityUtils to read json as string
                    String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    EntityUtils.consume(entity);
                    SessionGateway session = new SessionGateway(strResponse);
                    User tempUser = new User();
                    //tempUser.setId();
                    tempUser.setLogin(userName);
                    tempUser.setPassword(HashUtils.getCryptoHash(password, "SHA-256"));
                    MainController.getInstance().setSession(session);
                    return session;
                case 401:
                    throw new UnauthorizedException("login failed");
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if(response != null) {
                    response.close();
                }
                if(httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }

    }
}
