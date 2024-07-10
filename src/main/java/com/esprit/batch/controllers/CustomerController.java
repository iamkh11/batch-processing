package com.esprit.batch.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

/**
 * @author Ibtissem Belkhir
 * @author Mohamed Khalfallah
 * @company  
//  */
// @Log4j2
// @RestController
// @RequestMapping("/customers")
// public class CustomerController {

//     private final JobLauncher jobLauncher;

//     private final Job importCustomerJob;

//     public CustomerController(JobLauncher jobLauncher, Job importCustomerJob){
//         this.jobLauncher = jobLauncher;
//         this.importCustomerJob = importCustomerJob;
//     }


//     @PostMapping("/importCustomers")
//     public ResponseEntity<String> importCsvToDBJob() {
//         JobParameters jobParameters = new JobParametersBuilder()
//             .addLong("startAt", System.currentTimeMillis())
//             .toJobParameters();

//         try {
//             long start = System.currentTimeMillis();
//             log.info("Batch Processing STARTED...");
//             jobLauncher.run(importCustomerJob, jobParameters);
//             long end = System.currentTimeMillis() - start;
//             log.info("Batch Processing ENDED IN ==> {}", formatDuration(end));
//             return ResponseEntity.ok("Customers imported successfully IN ==> " + formatDuration(end));
//         } catch (Exception e) {
//             log.info(e.getMessage());
//             e.printStackTrace();
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import customers");
//         }
//     }


//     public static String formatDuration(long millis) {
//         long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
//         millis -= TimeUnit.MINUTES.toMillis(minutes);
//         long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
//         millis -= TimeUnit.SECONDS.toMillis(seconds);

//         return String.format("%dm %ds %dms", minutes, seconds, millis);
//     }


// }
