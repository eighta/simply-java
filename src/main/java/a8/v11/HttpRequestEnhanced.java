package a8.v11;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HttpRequestEnhanced {

    public static void main(String ... args) throws IOException, InterruptedException {
        jep321();
    }

    //JDK Enhancement Proposal
    //JEP 321: HTTP Client (Standard)
    //the API is fully asynchronous
    private static void jep321() throws IOException, InterruptedException {
        //https://dzone.com/articles/java-11-a-new-way-to-handle-http-and-websockets-in?utm_source=Top%205&utm_medium=email&utm_campaign=Top%205%202018-10-053
        //https://hackernoon.com/javahttp2-3a9b398c826d

        //The new APIs provide native support for HTTP 1.1/2 WebSocket
/*
        The core classes and interface providing the core functionality include:

        The HttpClient class, java.net.http.HttpClient
        The HttpRequestEnhanced class, java.net.http.HttpRequestEnhanced
        The HttpResponse<T> interface, java.net.http.HttpResponse
        The WebSocket interface, java.net.http.WebSocket
*/

        //THE CLIENT
        HttpClient httpClient =
                    HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .proxy(ProxySelector.getDefault() )
                        .build();

        //THE REQUEST
        URI END_POINT = URI.create("https://randomuser.me/api/");

        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(END_POINT)
                .timeout(Duration.ofMinutes(1))
                .build();

        //SENDING THE REQUEST AND GETTING THE RESPONSE
        HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, asString);

        int responseCode = httpResponse.statusCode();
        System.out.println("responseCode: " + responseCode);

        HttpHeaders headers = httpResponse.headers();
        printMap(headers.map());

        String body = httpResponse.body();
        System.out.println(body);
    }

    private static void printMap(Map<String, List<String>> map) {
        map.forEach( (k,v)-> System.out.println(k+": "+v.get(0)));
    }

    //Using regular API
    private void regularHttpRequest() throws IOException {

        //https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
        //https://www.mkyong.com/java/how-to-get-http-response-header-in-java/

        String endPoing = "https://randomuser.me/api/";

        URL obj = new URL(endPoing);
        //HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        Map<String, List<String>> responseHeaders = con.getHeaderFields();
        printMap(responseHeaders);



        //responseHeaders.keySet().forEach(System.out::println);


        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }
}
