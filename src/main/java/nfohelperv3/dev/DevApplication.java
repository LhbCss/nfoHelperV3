package nfohelperv3.dev;

import BO.movie;
import com.thoughtworks.xstream.XStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevApplication.class, args);
        XStream xStream = new XStream();
        xStream.alias("movie", movie.class);
        xStream.allowTypeHierarchy(movie.class);
         movie movie = (movie) xStream.fromXML(
                 DevApplication.class.getResource("/Files/movie.nfo"));
         System.out.println(movie);
    }
}
