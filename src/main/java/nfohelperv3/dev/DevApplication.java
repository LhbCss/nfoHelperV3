package nfohelperv3.dev;


import Interface.IOInterface;
import Interface.TaskInterface;
import Service.IOService;
import Service.TaskService;
import org.jdom2.JDOMException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DevApplication {
    public static void main(String[] args) throws IOException, JDOMException {
        SpringApplication.run(DevApplication.class, args);
//        DevApplication.class.getResource("/Files/movie.nfo");
//        Document document = new SAXBuilder().build(new File(Objects.requireNonNull(DevApplication.class.getResource("/Files/movie.nfo")).getFile()));
//        System.out.println("CLASSPATH: " + DEV_CLASSPATH);
//        Element rootElement = document.getRootElement();
//        List<Element> elements = rootElement.getChildren();
//        for (Element element : elements) {
//            if (!element.getChildren().isEmpty()) {
//                List<Element> children = element.getChildren();
//                for (Element child : children) {
//                    System.out.println(child.getName() + " - " + child.getText());
//                }
//            }
//            System.out.println(element.getName() + " - " + element.getText());
//        }
//        rootElement.addContent(new Element("test").setText("teest!"));
//        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
//        System.out.println(document.getBaseURI().substring(6));
//        outputter.output(document, new FileOutputStream(document.getBaseURI().substring(6)));
        TaskInterface taskInterface = new TaskService(new IOService());
        taskInterface.showTaskList();
        taskInterface.handleSelect();
    }
}
