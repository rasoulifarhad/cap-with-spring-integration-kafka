package com.farhad.example.cap.edge.boot;


import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.farhad.example.cap.edge.gateway.CapitalizeService;
import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class Runner {
  
    

    // @Bean
    // @Order(1000)
    public CommandLineRunner runner(CapitalizeService capitalizeService) {

        return args -> {

            Faker faker = new Faker();

            List<String> candidatesForCapitalize = Arrays.asList(
                                                        faker.lorem().word(),
                                                        faker.lorem().word(),
                                                        faker.lorem().word());
            candidatesForCapitalize
                    .forEach(capitalizee -> 
                        log.info("Boot: {} Capitalized to {}",
                                        capitalizee,
                                        capitalizeService.capitalize(capitalizee)));
                        
        };
    }
}
