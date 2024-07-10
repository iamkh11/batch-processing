package com.esprit.batch.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esprit.batch.config.BatchConfigMutex;

import lombok.extern.log4j.Log4j2;


/**
 * @author Ibtissem Belkhir
 * @author Mohamed Khalfallah
 * @company  
*/
@Log4j2
@RestController
@RequestMapping("/customers")
public class MutexController {

    private final BatchConfigMutex batchConfiguration;

    /**
     * Constructor to initialize the batch configuration.
     *
     * @param BatchConfigMutex the batch configuration.
    */
    public MutexController(BatchConfigMutex batchConfiguration) {
        this.batchConfiguration = batchConfiguration;
    }

    /**
     * Endpoint to trigger the import of customers from a CSV file.
     *
     * @return ResponseEntity with the status of the import process.
    */
    @PostMapping("/importCustomers")
    public ResponseEntity<String> importCsvToDBJob() {
        try {
            batchConfiguration.startThreads();
            return ResponseEntity.ok("Customer import started successfully...");
        } catch (Exception e) {
            log.error("Failed to start customer import: ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import customers");
        }
    }




}