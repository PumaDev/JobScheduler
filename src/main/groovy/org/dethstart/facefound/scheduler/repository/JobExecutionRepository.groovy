package org.dethstart.facefound.scheduler.repository

import org.dethstart.facefound.scheduler.domain.JobExecution
import org.dethstart.facefound.scheduler.domain.JobStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

import java.sql.Timestamp

interface JobExecutionRepository extends JpaRepository<JobExecution, String> {

    List<JobExecution> findJobExecutionByJobStatusAndLessStartDate(JobStatus jobStatus, Timestamp timestamp, Pageable pageable)
}
