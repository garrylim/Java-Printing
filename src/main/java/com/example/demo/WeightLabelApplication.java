package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.example.demo.Component.WeightIndicatorReader;

@SpringBootApplication
public class WeightLabelApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeightLabelApplication.class, args);
    }

    @Component
    public static class WeightIndicatorRunner implements ApplicationRunner {

        @Autowired
        private WeightIndicatorReader weightIndicatorReader;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            weightIndicatorReader.readDataFromWeightIndicator();
        }
    }
}
