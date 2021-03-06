package org.dethstart.facefound.scheduler.domain

import org.dethstart.facefound.scheduler.converter.JpaInstantConverter

import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table
import java.time.Instant

@Entity
@Table(name = 'job_execution')
class JobExecution {

    @Id
    @Column(name = 'id', nullable = false)
    String id

    @Column(name = 'job_type', nullable = false)
    String jobType

    @Enumerated(EnumType.STRING)
    @Column(name = 'status', nullable = false)
    JobStatus jobStatus

    @Lob
    @Column(name = 'job_details')
    String jobDetails

    @Column(name = "start_date")
    @Convert(converter = JpaInstantConverter)
    Instant startDate

    @Column(name = "end_date")
    @Convert(converter = JpaInstantConverter)
    Instant endDate

    @Column(name = 'error_message')
    String errorMessage

    @Lob
    @Column(name = 'error_details')
    String errorDetails

    @Column(name = "next_run_date")
    @Convert(converter = JpaInstantConverter)
    Instant nextRunDate

    @Column(name = "created_date")
    @Convert(converter = JpaInstantConverter)
    Instant createdDate
}
