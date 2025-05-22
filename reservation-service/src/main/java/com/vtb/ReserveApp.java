package com.vtb;

import domains.repositories.BookingItemRepository;
import domains.repositories.ReservationRepository;
import domains.repositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"domains", "services", "controllers"})
@EnableJpaRepositories(basePackageClasses = {ReservationRepository.class,
        BookingItemRepository.class, UserRepository.class})
@EntityScan(basePackages = {"items", "reserve", "clients"})
public class ReserveApp {
    public static void main( String[] args )
    {
        SpringApplication.run(ReserveApp.class, args);
    }
}
