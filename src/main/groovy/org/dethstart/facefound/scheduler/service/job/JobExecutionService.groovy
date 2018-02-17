package org.dethstart.facefound.scheduler.service.job

import org.dethstart.facefound.scheduler.domain.JobExecution

interface JobExecutionService {

    JobExecution getJobExecutionForRun()

    JobExecution update(JobExecution jobExecution)
}