package querydsl.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BasicApplication {

//    @Bean
//    JPAQueryFactory jpaQueryFactory(EntityManager em){
//        return new JPAQueryFactory(em);
//    }
    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }

}
