package com.gokhana.userimporter.listener

import com.gokhana.userimporter.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import java.util.function.Consumer


@Component
class JobCompletionNotificationListener : JobExecutionListener {

    private val log: Logger = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    override fun beforeJob(jobExecution: JobExecution) {
        log.info("Job is starting Name: ${jobExecution.jobInstance.jobName} Id: ${jobExecution.jobInstance.id}")
    }

    override fun afterJob(jobExecution: JobExecution) {
        log.info("Job is finished ${jobExecution.exitStatus} Name: ${jobExecution.jobInstance.id} Id: ${jobExecution.jobId}")
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
            jdbcTemplate.query<Any>(
                "SELECT name, surname FROM users"
            ) { rs: ResultSet, row: Int ->
                User(
                    rs.getString(1),
                    rs.getString(2)
                )
            }.forEach(Consumer { person: Any -> log.info("Found <$person> in the database.") })        }
    }
}