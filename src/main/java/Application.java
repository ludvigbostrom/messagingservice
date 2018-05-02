import Storage.DBAccessor;
import Storage.DataStore;
import Storage.LocalStore;
import model.MessageReq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class Application {
    private DataStore dataStore;

    public Application() {
        dataStore = new DBAccessor();
        try {
            ((DBAccessor)dataStore).openConnection();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to database. Using local storage.");
            dataStore = new LocalStore();
        }
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public String sendMessage(@RequestBody MessageReq message) {
        try {
            dataStore.addMessage(message.toMessage());
        } catch (SQLException e) {
            return "SQL fail";
        }
        return "success";
    }


    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public Object getMessages(@RequestParam String username, @RequestParam boolean onlyNew) {
        try {
            if (onlyNew)
                return dataStore.getUnreadMessages(username);
            return dataStore.getMessages(username);
        } catch (SQLException e) {
            return "SQL fail.";
        }
    }

    @RequestMapping(value = "/messageInRange", method = RequestMethod.GET)
    public Object getMessagesInRange(@RequestParam String username, @RequestParam boolean onlyNew, @RequestParam long fromTimestamp, @RequestParam long toTimestamp) {
        try {
            if (onlyNew)
                return dataStore.getUnreadMessagesInRange(username, fromTimestamp, toTimestamp);
            return dataStore.getMessagesInRange(username, fromTimestamp, toTimestamp);
        } catch (SQLException e) {
            return "SQL fail.";
        }
    }

    @RequestMapping(value = "/message", method = RequestMethod.DELETE)
    public String deleteMessage(@RequestParam String id) {
        try {
            long numId = Long.parseLong(id);
            dataStore.deleteMessage(numId);
        } catch (SQLException e) {
            return "SQL fail";
        }
        catch (NumberFormatException nfe) {
            return "Not a numeric id.";
        }
        return "success";
    }

    @RequestMapping(value = "/messages", method = RequestMethod.DELETE)
    public String deleteMessages(@RequestParam List<String> ids) {
        try {
            List<Long> numIds = ids.stream().map(Long::parseLong).collect(Collectors.toList());
            dataStore.deleteMessages(numIds);
        } catch (SQLException e) {
            return "SQL fail";
        }
        catch (NumberFormatException nfe) {
            return "Not a numeric id.";
        }
        return "success";
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}