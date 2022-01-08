package mvc.gateway;

import com.sun.tools.javac.Main;
import mvc.model.AuditTrail;
import mvc.model.AuditTrailEntry;
import mvc.model.Person;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.rmi.activation.UnknownGroupException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mvc.model.User;
import mvc.screens.MainController;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class PersonGateway {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "http://localhost:8080";

    public PersonGateway() {

    }


    // fetchPeople or listPeople if that's your preference :)
    public static List<Person> fetchPeople(String token) throws UnauthorizedException, UnknownException
    {
        List<Person> people = new ArrayList<Person>();

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // This works somehow
            HttpGet getRequest = new HttpGet("http://localhost:8080/people");

            JSONObject tempObj3 = new JSONObject(token);

            getRequest.setHeader("Authorization", tempObj3.getString("id"));
            CloseableHttpResponse response = httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            // use org.apache.http.util.EntityUtils to read json as string
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            JSONArray personList = new JSONArray(strResponse);

            // Spaghetti code time :)
            for(Object tempObj : personList)
            {
                //System.out.println("tempOBJ = " + tempObj.toString());
                JSONObject jsonobj = (JSONObject) tempObj;
                //System.out.println("jsonobj = " + jsonobj.toString());
                Person tempPerson = new Person();
                tempPerson.setId(jsonobj.getInt("id"));
                tempPerson.setFirstName(jsonobj.getString("firstName"));
                tempPerson.setLastName(jsonobj.getString("lastName"));
                tempPerson.setDob(LocalDate.parse(jsonobj.getString("dob")));
                people.add(tempPerson);

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return people;
    }

    private String executeGetRequest(String url, String token) throws UnauthorizedException, UnknownException
    {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;


        try {
            httpclient = HttpClients.createDefault();
            HttpGet getRequest = new HttpGet(url);

            if(token != null && token.length() > 0)
                getRequest.setHeader("Authorization", token);

            response = httpclient.execute(getRequest);


            switch(response.getStatusLine().getStatusCode())
            {
                case 200:
                    return getStringFromResponse(response);
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);

            }
        }
    }

    private static String getStringFromResponse(CloseableHttpResponse response) throws IOException
    {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);
        return strResponse;
    }

    // addPerson or insertPerson if thats your preference :)
    public static void addPerson(String token, Person person)
    {
        AuditGateway auditGateway = new AuditGateway();
        try {

            String url = URL + "/people";


            CloseableHttpClient httpclient = null;
            CloseableHttpResponse response = null;

            try {
                httpclient = HttpClients.createDefault();
                HttpPost postRequest = new HttpPost(url);

                postRequest.setHeader("Accept", "application/json");
                postRequest.setHeader("Content-type", "application/json");

                if(token != null && token.length() > 0)
                    postRequest.setHeader("Authorization", token);

                LOGGER.info("POST REQUEST  = " + postRequest.toString() + token);

                // Put our person data into a form that can be ran against json for validation
                JSONObject formData = new JSONObject();
                formData.put("firstName", person.getFirstName());
                formData.put("lastName", person.getLastName());
                formData.put("dob", person.getDob().toString());

                String formDataString = formData.toString();
                StringEntity reqEntity = new StringEntity(formDataString);

                postRequest.setEntity(reqEntity);

                // execute the request after setting all the appropriate for data
                response = httpclient.execute(postRequest);
                switch(response.getStatusLine().getStatusCode())
                {
                    case 200:
                        System.out.println("AUDIT STARTS HERE");


                        break;
                    case 400:
                        throw new UnauthorizedException("Invalid person entry");
                    default:
                        //LOGGER.info("DEFAULT ERROR IN PERSON GATEWAY");
                        throw new UnknownException(response.getStatusLine().getReasonPhrase());

                }

            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);

            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    if (httpclient != null){
                        httpclient.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new UnauthorizedException(e);

                }
            }

        } catch (RuntimeException e) {
            throw new UnknownException(e);
        }


    }

    // updatePerson
    public static void updatePerson(String token, Person person)
    {

        System.out.println("PERSON GATEWAY: PERSON ID = " + person.getId());
        String url = URL + "/people/" + person.getId();
        System.out.println("URL = " + url);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;


        try {
            httpclient = HttpClients.createDefault();
            HttpPut putRequest = new HttpPut(url);
            putRequest.setHeader("Accept", "application/json");
            putRequest.setHeader("Content-type", "application/json");


            if(token != null && token.length() > 0)
                putRequest.setHeader("Authorization", token);

            JSONObject formData = new JSONObject();
            formData.put("firstName", person.getFirstName());
            formData.put("lastName", person.getLastName());
            formData.put("dob", person.getDob().toString());

            String formDataString = formData.toString();
            StringEntity reqEntity = new StringEntity(formDataString);

            putRequest.setEntity(reqEntity);

            //System.out.println("PERSON GATEWAY: PERSON == " + person.toString());
            System.out.println("PERSON GATEWAY: TOKEN == " + token);
            System.out.println("PERSON GATEWAY: REQENTITY == " + formDataString);
            response = httpclient.execute(putRequest);
            System.out.println("PERSON GATEWAY: RESPONSE == " + response.getStatusLine());

            switch(response.getStatusLine().getStatusCode())
            {
                case 200:
                    LOGGER.info(getStringFromResponse(response));
                    // We do person.getID() - 1... because reasons that I can't remember.
                    System.out.println("Person id = " + person.getId());
                    //MainController.getInstance().getPerson().set(person.getId() - 1, person);
                    return;
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    System.out.println(response.getStatusLine());
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);

            }
        }



    }


    // deletePerson
    public static void deletePerson(String token, Person person, String url)
    {

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpDelete deleteRequest = new HttpDelete(url);


            if(token != null && token.length() > 0)
                deleteRequest.setHeader("Authorization", token);

            response = httpclient.execute(deleteRequest);

            switch(response.getStatusLine().getStatusCode())
            {
                case 200:
                    LOGGER.info("DELETING PERSON: " + person.toString());
                    // Its an array list so just remove.
                    MainController.getInstance().getPerson().remove(person);
                    break;
                case 401:
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);

            }
        }

    }

    public static ArrayList<AuditTrail> fetchAuditTrail(String token, Person person, String url)
    {
        ArrayList<AuditTrail> auditTrailList = new ArrayList<AuditTrail>();
        if(person == null)
            return null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String auditURL = URL + "/people/" + person.getId() +"/"+ "audittrail";

            // This works somehow
            HttpGet getRequest = new HttpGet(auditURL);

            JSONObject tempObj3 = new JSONObject(token);

            getRequest.setHeader("Authorization", tempObj3.getString("id"));
            CloseableHttpResponse response = httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            // use org.apache.http.util.EntityUtils to read json as string
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            if(response.getStatusLine().getStatusCode() == 200){

                JSONArray personList = new JSONArray(strResponse);
                //System.out.println("PERSON LIST = " + personList.toString());

                for(Object tempObj : personList) {
                    //System.out.println("tempOBJ = " + tempObj.toString());
                    JSONObject jsonobj = (JSONObject) tempObj;
                    //System.out.println("jsonobj = " + jsonobj.toString());
                    AuditTrail auditTrailEntry = new AuditTrail();
                    Object tempJson = jsonobj.get("changedBy");
                    JSONObject jsonobj2 = (JSONObject) tempJson;
                    User tempUser = new User();

                    // I am laughing uncontrollably this code is literally so awful

                    tempUser.setLogin(jsonobj2.getString("login"));
                    tempUser.setPassword(jsonobj2.getString("password"));
                    tempUser.setId(jsonobj2.getInt("id"));
                    auditTrailEntry.setChangedBy(tempUser);
                    auditTrailEntry.setWhenOccurred(Instant.parse(jsonobj.getString("whenOccurred")));
                    Object tempJsonPerson = jsonobj.get("person");
                    JSONObject jsonobj3 = (JSONObject) tempJsonPerson;
                    Person tempPerson = new Person();
                    tempPerson.setDob(LocalDate.parse(jsonobj3.getString("dob")));
                    tempPerson.setId(jsonobj3.getInt("id"));
                    tempPerson.setLastName(jsonobj3.getString("lastName"));
                    tempPerson.setFirstName(jsonobj3.getString("firstName"));
                    auditTrailEntry.setPerson(tempPerson);
                    auditTrailEntry.setId(jsonobj.getInt("id"));
                    auditTrailEntry.setChangeMsg(jsonobj.getString("changeMsg"));

                    auditTrailList.add(auditTrailEntry);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return auditTrailList;
    }

    public static Person fetchPerson(String token, int id) throws UnauthorizedException, UnknownException
    {
        Person tempPerson = new Person();

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // This works somehow
            HttpGet getRequest = new HttpGet("http://localhost:8080/people/" + id);
            System.out.println("TOKEN = " + token);
            JSONObject tempObj3 = new JSONObject(token);

            getRequest.setHeader("Authorization", tempObj3.getString("id"));
            CloseableHttpResponse response = httpclient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            // use org.apache.http.util.EntityUtils to read json as string
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);


            //System.out.println("PERSON LIST = " + personList.toString());
            JSONObject personObj = new JSONObject(strResponse);
            tempPerson.setId(personObj.getInt("id"));
            tempPerson.setLastModified(LocalDateTime.parse(personObj.getString("lastModified")));
            tempPerson.setDob(LocalDate.parse(personObj.getString("dob")));
            tempPerson.setFirstName(personObj.getString("firstName"));
            tempPerson.setLastName(personObj.getString("lastName"));


            return tempPerson;

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return tempPerson;
    }
}
