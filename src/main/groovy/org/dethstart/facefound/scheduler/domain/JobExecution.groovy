package org.dethstart.facefound.scheduler.domain

import java.time.Instant

class JobExecution {
    String id
    String jobType
    String jobDetails
    String jobStatus
    String errorMessage
    String errorDetails
    Instant startDate
    Instant endDate
    Instant lastMonitoredDate
    Instant createdDate
}
