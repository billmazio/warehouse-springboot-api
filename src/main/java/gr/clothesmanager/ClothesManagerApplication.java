package gr.clothesmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "gr.clothesmanager")
@EnableJpaAuditing
@EnableScheduling
public class ClothesManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClothesManagerApplication.class, args);
    }
}
