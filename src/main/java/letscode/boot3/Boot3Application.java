package letscode.boot3;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

@Slf4j
@ServletComponentScan
@SpringBootApplication
public class Boot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Boot3Application.class, args);
    }


    @Bean
    ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> log.info("the web server is ready to serve HTTP traffic on port {}",
                event.getWebServer().getPort());
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> log.info("the service is healthy? {}", event.getState().toString());
    }

/*
    @Bean
    ServletRegistrationBean<LegacyServlet> legacyServlet() {
        var ls = new LegacyServlet();
        var srb = new ServletRegistrationBean<LegacyServlet>();
        srb.setServlet(ls);
        srb.addUrlMappings("/legacy");
        return srb;
    }*/

}

class Legacy {
    static final String PATH = "/legacy";
}

@WebFilter(urlPatterns = Legacy.PATH)
class LegacyFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("before the request");
        super.doFilter(request, response, chain);
        System.out.println("after the request");
    }
}

@WebServlet(urlPatterns = Legacy.PATH)
class LegacyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("got the servlet request");
        resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        resp.getWriter().println("OK");
    }
}