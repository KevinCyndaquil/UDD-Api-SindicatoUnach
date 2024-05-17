package unach.sindicato.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class UddApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UddApiApplication.class, args);
    }
}
