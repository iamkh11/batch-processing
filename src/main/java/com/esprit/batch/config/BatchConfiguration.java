package com.esprit.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.esprit.batch.beans.Customer;
import com.esprit.batch.repositories.CustomerRepository;

import lombok.extern.log4j.Log4j2;

/**
 * @author Mohamed Khalfallah
 * @company  
 */
@Log4j2
@Configuration
@EnableAutoConfiguration
public class BatchConfiguration {

    @Value(value = "${inputFile}")
    private Resource inputResource;

    // @Bean
    // FlatFileItemReader<Customer> reader() {
    //     return new FlatFileItemReaderBuilder<Customer>()
    //             .name("customerItemReader")
    //             .resource(inputResource)
    //             .linesToSkip(1) // Skip header
    //             .delimited()
    //             .names("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob")
    //             .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
    //                 setTargetType(Customer.class);
    //             }})
    //             .build();
    // }

    @Bean
    FlatFileItemReader<Customer> reader() {
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


    @Bean
    ItemProcessor<Customer, Customer> processor() {
        return customer -> customer; // Pass-through processor
    }

    @Bean
    RepositoryItemWriter<Customer> writer(CustomerRepository customerRepository) {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    // @Bean
    // Job importCustomerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    //                              ItemReader<Customer> reader, ItemProcessor<Customer, Customer> processor,
    //                              ItemWriter<Customer> writer) {
    //     StepBuilder stepBuilder = new StepBuilder("step1", jobRepository);
    //     Step step = stepBuilder.<Customer, Customer>chunk(10, transactionManager)
    //             .reader(reader)
    //             .processor(processor)
    //             .writer(writer)
    //             .build();

    //     JobBuilder jobBuilder = new JobBuilder("importCustomerJob", jobRepository);
    //     return jobBuilder
    //             .incrementer(new RunIdIncrementer())
    //             .start(step)
    //             .build();
    // }


    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor() {
            @Override
            public void execute(Runnable task) {
                super.execute(() -> {
                    try {
                        log.info("Starting Task in thread: {}", Thread.currentThread().getName());
                        task.run();
                    } finally {
                        log.info("Task Finished in thread: {}", Thread.currentThread().getName());
                    }
                });
            }
        };
        taskExecutor.setCorePoolSize(10); // Set the number of threads
        taskExecutor.setMaxPoolSize(20); // Set the maximum number of threads
        taskExecutor.setQueueCapacity(25); // Set the queue capacity
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }


    @Bean
    Job importCustomerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                ItemReader<Customer> reader, ItemProcessor<Customer, Customer> processor,
                                ItemWriter<Customer> writer, TaskExecutor taskExecutor) {
        StepBuilder stepBuilder = new StepBuilder("step1", jobRepository);
        Step step = stepBuilder.<Customer, Customer>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor) // Use the TaskExecutor
                .build();

        JobBuilder jobBuilder = new JobBuilder("importCustomerJob", jobRepository);
        return jobBuilder
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
