package utils.tests;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TestApiSession {

    public static final String LOCAL_ENV          = "local";
    public static final String DEV_ENV            = "dev";

    private String             env                = "local";
    private String             token;
    private String             email;
    private String             firebaseWebAuthKey = "";

    public TestApiSession() {
        this(LOCAL_ENV);
    }

    public TestApiSession(String env) {
        this.env = env;
    }

    public String getApiUrl() {
        StringBuilder str = new StringBuilder();

        if (env.equals(LOCAL_ENV)) {
            str.append("http://localhost:8085/v1");
        } else if (env.equals(DEV_ENV)) {
            str.append("https://dev-api.poochapp.net/v1");
        }

        return str.toString();
    }

}
