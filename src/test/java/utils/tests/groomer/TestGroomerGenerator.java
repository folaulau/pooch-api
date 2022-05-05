package utils.tests.groomer;

import utils.tests.TestApiService;
import utils.tests.TestApiSession;

public class TestGroomerGenerator {

    private TestApiService testApiService;

    public TestGroomerGenerator() {

        TestApiSession apiSession = new TestApiSession(TestApiSession.DEV_ENV);
        
        testApiService = new TestApiService(apiSession);

    }

    public static void main(String[] args) {
        new TestGroomerGenerator();
    }

    public void signUp() {

    }
}
