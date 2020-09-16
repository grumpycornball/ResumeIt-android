package chags.ph.sync.droid.pojo;

public class UserData {

    protected String UUID,email,androidToken,webToken;

    public UserData()
    {

    }
    public UserData(String UUID, String email, String androidToken, String webToken) {
        this.UUID = UUID;
        this.email = email;
        this.androidToken = androidToken;
        this.webToken = webToken;
    }

    public String getUUID() {

        if(this.UUID==null || this.UUID=="")
        {
            return "NULL";
        }
        return this.UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getEmail() {
        if(this.email==null || this.email=="")
        {
            return "NULL";
        }
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAndroidToken() {
        if(this.androidToken==null || this.androidToken=="")
        {
            return "NULL";
        }
        return this.androidToken;
    }

    public void setAndroidToken(String androidToken) {
        this.androidToken = androidToken;
    }

    public String getWebToken() {
        if(this.webToken==null || this.webToken=="")
        {
            return "NULL";
        }
        return this.webToken;
    }

    public void setWebToken(String webToken) {
        this.webToken = webToken;
    }

    public String getPipeDelimited()
    {
        return getUUID()+"|"+getEmail()+"|"+getAndroidToken()+"|"+getWebToken()+"|";

    }
}
