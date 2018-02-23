package org.dethstart.facefound.scheduler.service.job

import org.dethstart.facefound.scheduler.domain.JobExecution

interface JobExecutionService {

    JobExecution create(JobExecution jobExecution)

    JobExecution getJobExecutionForRun()

    JobExecution findById(String id)

    JobExecution update(JobExecution jobExecution)
}