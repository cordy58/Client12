package Data;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.FindFamilyEventsResult;
import Result.FindFamilyResult;
import Result.LoginResult;
import Result.RegisterResult;

public class ServerProxy {
    private String serverHost;
    private String serverPort;

    public void ServerProxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    //login
    public LoginResult login(LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);
                System.out.println(respData);
                return result;
            } else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);
                System.out.println(respData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult(false, e.getMessage(), null, null, null);
        }
    }
    //register
    public RegisterResult register(RegisterRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                System.out.println(respData);
                return result;
            } else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                System.out.println(respData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult(null, null, null, e.getMessage(), false);
        }
    }
    //getPeople
    public FindFamilyResult findFamily(String authToken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                FindFamilyResult result = gson.fromJson(respData, FindFamilyResult.class);
                System.out.println(respData);
                return result;
            } else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                FindFamilyResult result = gson.fromJson(respData, FindFamilyResult.class);
                System.out.println(respData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new FindFamilyResult(null, false, e.getMessage());
        }
    }
    //getEvents
    public FindFamilyEventsResult findFamilyEvents(String authToken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                FindFamilyEventsResult result = gson.fromJson(respData, FindFamilyEventsResult.class);
                System.out.println(respData);
                return result;
            } else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                FindFamilyEventsResult result = gson.fromJson(respData, FindFamilyEventsResult.class);
                System.out.println(respData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new FindFamilyEventsResult(null, false, e.getMessage());
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}
