package org.dethstart.facefound.scheduler.service.job

import org.dethstart.facefound.scheduler.domain.JobExecution
import org.dethstart.facefound.scheduler.domain.JobStatus
import org.dethstart.facefound.scheduler.exception.NotFoundException
import org.dethstart.facefound.scheduler.repository.JobExecutionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

import java.time.Instant

@Service
class JobExecutionServiceImpl implements JobExecutionService {

    @Autowired
    JobExecutionRepository jobExecutionRepository

    @Override
    JobExecution create(JobExecution jobExecution) {
        jobExecution.id = UUID.randomUUID().toString()
        jobExecution.createdDate = Instant.now()
        return jobExecutionRepository.save(jobExecution)
    }

    @Override
    JobExecution getJobExecutionForRun() {
        List<JobExecution> jobExecutionsForRun = jobExecutionRepository.findJobExecutionByJobStatusAndNextRunDateLessThan(JobStatus.NEW, Instant.now(), new PageRequest(0, 1))
        return jobExecutionsForRun ? jobExecutionsForRun[0] : null
    }

    @Override
    JobExecution findById(String id) {
        return jobExecutionRepository.findOne(id)
    }

    @Override
    JobExecution update(JobExecution jobExecution) {
        JobExecution existingJobExecution = jobExecutionRepository.findOne(jobExecution.id)

        if (!existingJobExecution) {
            throw new NotFoundException()
        }

        return jobExecutionRepository.save(jobExecution)
    }
}
