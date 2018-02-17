package org.dethstart.facefound.scheduler.service.job

import groovy.util.logging.Log
import org.dethstart.facefound.scheduler.domain.JobExecution
import org.dethstart.facefound.scheduler.domain.JobStatus
import org.dethstart.facefound.scheduler.exception.BusyJobRunnerException
import org.dethstart.facefound.scheduler.util.ExceptionUtil

import java.util.logging.Level

import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

@Log
class JobRunner implements Runnable {
    private static Integer counter = 0
    private Integer number = counter++

    private AtomicBoolean active

    private JobExecution jobExecution

    private ScheduleJobService scheduleJobService
    private JobExecutionService jobExecutionService

    private Integer pause

    private Queue<JobRunner> freeJobRunners

    JobRunner(ScheduleJobService scheduleJobService, JobExecutionService jobExecutionService, Integer pauseTime, Queue<JobRunner> freeJobRunners) {
        this.scheduleJobService = scheduleJobService
        this.jobExecutionService = jobExecutionService
        this.pause = pauseTime
        this.freeJobRunners = freeJobRunners
        this.active = new AtomicBoolean(true)
    }

    void finish() {
        active.set(false)
    }

    synchronized void setJobExecution(JobExecution newJobExecution) throws BusyJobRunnerException {
        if (jobExecution) {
            throw new BusyJobRunnerException("JobRunner #$number: Job runner already has job")
        } else {
            this.jobExecution = new JobExecution()
        }
    }

    @Override
    void run() {
        log.info("JobRunner #$number: JobRunner start")
        while (active.get()) {
            if (jobExecution) {
                processJob()
                jobExecution = null
                freeJobRunners.add(this)
            }
            sleep()
        }
        log.info("JobRunner #$number: JobRunner start")
    }

    private void sleep() {
        try {
            Thread.sleep(pause)
        } catch (InterruptedException ex) {
            log.log(Level.WARNING, "JobRunner #$number: Thread was interrupt. More info: ${ex.message}")
        }
    }

    private void processJob() {
        ScheduleJob scheduleJob = scheduleJobService.getScheduleJob(jobExecution.jobType)
        if (scheduleJob) {
            jobExecution = runJob(scheduleJob, jobExecution)
        } else {
            log.info("JobRunner #$number: Job ${jobExecution.jobType} doesn't found")
            jobExecution = updateJobExecutionToNotFoundStatus(jobExecution)
        }
        jobExecution.endDate = Instant.now()
        jobExecutionService.update(jobExecution)
    }

    private JobExecution runJob(ScheduleJob scheduleJob, JobExecution jobExecution) {
        try {
            scheduleJob.run(jobExecution.jobDetails)
            jobExecution = updateJobExecutionToSuccessStatus(jobExecution)
        } catch (Throwable exception) {
            log.info("JobRunner #$number: Job ${jobExecution.jobType} was faild with message ${exception.message}")
            jobExecution = updateJobExecutionToErrorStatus(jobExecution, exception)
        }
        return jobExecution
    }

    private static JobExecution updateJobExecutionToSuccessStatus(JobExecution jobExecution) {
        jobExecution.jobStatus = JobStatus.COMPLETE
        return jobExecution
    }

    private static JobExecution updateJobExecutionToErrorStatus(JobExecution jobExecution, Throwable exception) {
        jobExecution.jobStatus = JobStatus.ERROR
        jobExecution.errorMessage = exception.message
        jobExecution.errorDetails = ExceptionUtil.getStackTraceAsString(exception)
        return jobExecution
    }

    private static JobExecution updateJobExecutionToNotFoundStatus(JobExecution jobExecution) {
        jobExecution.jobStatus = JobStatus.ERROR
        jobExecution.errorMessage = "Job type doesn't found"
        return jobExecution
    }
}
