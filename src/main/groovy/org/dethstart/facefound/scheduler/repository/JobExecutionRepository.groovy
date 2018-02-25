package org.dethstart.facefound.scheduler.repository

import org.dethstart.facefound.scheduler.domain.JobExecution
import org.dethstart.facefound.scheduler.domain.JobStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

import java.time.Instant

interface JobExecutionRepository extends JpaRepository<JobExecution, String> {

    List<JobExecution> findJobExecutionByJobStatusAndNextRunDateLessThan(JobStatus jobStatus, Instant time, Pageable pageable)
}
