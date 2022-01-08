package mvc.gateway;

public class SessionGateway
{
    private String sessionID;
    private String userFullName;




    public SessionGateway(String sessionID)
    {
        this.sessionID = sessionID;
        this.userFullName = "";
    }

    public SessionGateway(String sessionID, String userFullName)
    {
        this.sessionID = sessionID;
        this.userFullName = userFullName;
    }

    @Override
    public String toString()
    {
        return "Session{" +
                "sessionId='" + sessionID + '\'' +
                ", userFullName='" + userFullName + '\'' +
                '}';

    }

    public String getSessionID() { return sessionID; }

    public void setSessionId(String sessionId) {
        this.sessionID = sessionId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }



}
