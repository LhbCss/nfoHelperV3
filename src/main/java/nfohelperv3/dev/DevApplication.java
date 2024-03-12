package nfohelperv3.dev;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static Constant.Constant.DEV_CLASSPATH;

@SpringBootApplication
public class DevApplication {
    public static void main(String[] args) throws IOException, JDOMException {
        SpringApplication.run(DevApplication.class, args);
        // DevApplication.class.getResource("/Files/movie.nfo")
        Document document = new SAXBuilder().build(new File(Objects.requireNonNull(DevApplication.class.getResource("/Files/movie.nfo")).getFile()));
        System.out.println("CLASSPATH: " + DEV_CLASSPATH);
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.getChildren();
        for (Element element : elements) {
            if (!element.getChildren().isEmpty()) {
                List<Element> children = element.getChildren();
                for (Element child : children) {
                    System.out.println(child.getName() + " - " + child.getText());
                }
            }
            System.out.println(element.getName() + " - " + element.getText());
        }
        rootElement.addContent(new Element("test").setText("teest!"));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        System.out.println(document.getBaseURI().substring(6));
        outputter.output(document, new FileOutputStream(document.getBaseURI().substring(6)));
    }
}
