import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class Consumer {
    public static void main(String[] args) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        String url = "http://94.198.50.185:7081/api/users";
        ResponseEntity<String> response =  restTemplate.getForEntity(url, String.class);

        String sessionId = response
                .getHeaders()
                .get("Set-Cookie")
                .toString()
                .substring(1, 44);
        System.out.println(sessionId);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Cookie", sessionId);

        User user = new User(3L, "James", "Brown", (byte) 35);

        HttpEntity<User> postUser = new HttpEntity<>(user, responseHeaders);
        String responsePost = restTemplate.postForObject(url, postUser, String.class);

        user.setName("Thomas");
        user.setLastName("Shelby");

        HttpEntity<User> putUser = new HttpEntity<>(user, responseHeaders);
        String responsePut = restTemplate.exchange(url, HttpMethod.PUT, putUser, String.class).getBody();

        HttpEntity<?> deleteUser = new HttpEntity<>(null, responseHeaders);
        String responseDelete = restTemplate.exchange(url + "/3", HttpMethod.DELETE, deleteUser, String.class).getBody();

        System.out.println(responsePost + responsePut + responseDelete );
    }
}