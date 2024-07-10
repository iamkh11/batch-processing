package com.esprit.batch.config;

import static com.esprit.batch.utils.FormatHelper.formatDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.esprit.batch.beans.Customer;
import com.esprit.batch.repositories.CustomerRepository;

import lombok.extern.log4j.Log4j2;

/**
 * BatchConfigMutex handles the configuration for the batch processing of customer data
 * from a CSV file using a producer-consumer pattern with mutex for synchronization.
 */
/**
 * @author Mohamed Khalfallah
 * @author Ibtissem Bilkhir
 * @company  
 */
@Log4j2
@Configuration
public class BatchConfigMutex {

    @Value("${inputFile}")
    private Resource inputResource;

    private final BlockingQueue<Customer> queue = new LinkedBlockingQueue<>(50000000);
    private final Object lock = new Object();

    private final CustomerRepository customerRepository;

    /**
     * Constructor to initialize the customer repository.
     *
     * @param customerRepository the customer repository.
    */
    public BatchConfigMutex(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Creates and configures a FlatFileItemReader to read customer data from a CSV file.
     *
     * @return a configured FlatFileItemReader.
    */
    public FlatFileItemReader<Customer> reader() {
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(inputResource)
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "address", "city", "state", "zipCode", "registrationDate", "lastLoginDate", "accountBalance")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    /**
     * Starts multiple producer and consumer threads to read from the CSV file and save data to the database.
     * Producers read data from the CSV file and put it into a shared queue.
     * Consumers read data from the queue and save it to the database in batches.
    */
    public void startThreads() {
        int numProducers = 10; // Number of producer threads
        int numConsumers = 10; // Number of consumer threads
        long start = System.currentTimeMillis();
        log.info("Batch Processing STARTED...");
        log.info("Starting producer and consumer threads...");
        for (int i = 0; i < numProducers; i++) {
            new Thread(() -> {
                FlatFileItemReader<Customer> reader = reader();
                ExecutionContext executionContext = new ExecutionContext();
                reader.open(executionContext);
                try {
                    Customer customer;
                    while ((customer = reader.read()) != null) {
                        synchronized (lock) {
                            queue.put(customer);
                            log.info("Produced: " + customer);
                        }
                    }
                } catch (Exception e) {
                    log.error("Producer Error: ", e);
                } finally {
                    reader.close();
                }
            }).start();
        }

        for (int i = 0; i < numConsumers; i++) {
            new Thread(() -> {
                List<Customer> batch = new ArrayList<>();
                try {
                    while (true) {
                        Customer customer;
                        synchronized (lock) {
                            customer = queue.poll(100, TimeUnit.MILLISECONDS);
                        }
                        if (customer != null) {
                            batch.add(customer);
                        }
                        if (batch.size() >= 100 || (!batch.isEmpty() && customer == null)) {
                            customerRepository.saveAll(batch);
                            log.info("Consumed and saved batch of size: " + batch.size());
                            batch.clear();
                        }
                    }
                } catch (Exception e) {
                    log.error("Consumer Error: ", e);
                }
            }).start();
        }

        long end = System.currentTimeMillis() - start;
        log.info("Batch Processing ENDED IN ==> {}", formatDuration(end));
    }


}
