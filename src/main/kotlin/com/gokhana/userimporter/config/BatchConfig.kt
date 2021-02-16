package com.gokhana.userimporter.config

import com.gokhana.userimporter.listener.JobCompletionNotificationListener
import com.gokhana.userimporter.model.User
import com.gokhana.userimporter.processor.UserItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class BatchConfig {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun reader(): FlatFileItemReader<User> {

        return FlatFileItemReaderBuilder<User>()
            .name("userItemReader")
            .resource(ClassPathResource("users.csv"))
            .linesToSkip(1)
            .strict(false)
            .encoding("ISO-8859-1")
            .lineMapper(object : DefaultLineMapper<User>() {
                init {
                    setLineTokenizer(object : DelimitedLineTokenizer() {
                        init {
                            setNames("name", "surname")
                        }
                    })
                    setFieldSetMapper(object : BeanWrapperFieldSetMapper<User>() {
                        init {
                            setTargetType(User::class.java)
                        }
                    })
                }
            })
            .build()
    }


    @Bean
    fun processor(): UserItemProcessor {
        return UserItemProcessor()
    }

    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<User> {
        return JdbcBatchItemWriterBuilder<User>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .sql("INSERT INTO users (name, surname) VALUES (:name, :surname)")
            .dataSource(dataSource)
            .build()
    }


    @Bean
    fun importUserJob(listener: JobCompletionNotificationListener, step1: Step): Job {
        return jobBuilderFactory.get("importUserJob")
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()
    }

    @Bean
    fun step1(writer: JdbcBatchItemWriter<User>): Step {
        return stepBuilderFactory.get("step1")
            .chunk<User, User>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build()
    }
}