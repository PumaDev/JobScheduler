package org.dethstart.facefound.scheduler.service.scheduler

import groovy.util.logging.Log4j
import org.dethstart.facefound.scheduler.domain.JobExecution
import org.dethstart.facefound.scheduler.domain.JobStatus
import org.dethstart.facefound.scheduler.service.job.JobExecutionService
import org.dethstart.facefound.scheduler.service.job.JobRunner
import org.dethstart.facefound.scheduler.service.job.ScheduleJobService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.transaction.Transactional
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

@Log4j
@Service
class SchedulerServiceImpl implements SchedulerService {

    @Value('${ff.scheduler.thread-pool.size:5}')
    Integer threadPoolSize

    @Value('${ff.scheduler.thread.pause:5}')
    Integer threadPause

    @Value('${ff.scheduler.monitoring.pause:5}')
    Integer monitoringPause

    @Autowired
    ScheduleJobService jobService

    @Autowired
    SchedulerService self

    @Autowired
    JobExecutionService jobExecutionService

    Queue<JobRunner> freeJobRunners = [] as LinkedList

    List<Thread> threads = []

    List<JobRunner> jobRunners = []

    AtomicBoolean active = new AtomicBoolean(true)

    @PostConstruct
    void init() {
        for (i in (1..threadPoolSize)) {
            JobRunner jobRunner = new JobRunner(jobService, jobExecutionService, threadPause, freeJobRunners)
            Thread thread = new Thread(jobRunner)

            freeJobRunners.add(jobRunner)
            jobRunners.add(jobRunner)
            threads.add(thread)
            thread.start()
        }
    }

    @Override
    void start() {
        while (active.get()) {
            if (!freeJobRunners.isEmpty()) {
                self.startJob()
            }

            Thread.sleep(monitoringPause)
        }
    }

    @Transactional
    void startJob() {
        JobExecution jobExecutionForRun = jobExecutionService.getJobExecutionForRun()
        if (jobExecutionForRun) {
            JobRunner freeJobRunner = freeJobRunners.poll()
            jobExecutionForRun.jobStatus = JobStatus.IN_PROGRESS
            jobExecutionForRun.startDate = Instant.now()
            try {
                jobExecutionService.update(jobExecutionForRun)
                freeJobRunner.setJobExecution(jobExecutionForRun)
            } catch (OptimisticLockingFailureException ex) {
                log.warn(ex)
            }
        }
    }

    @Override
    @PreDestroy
    void finish() {
        active.set(false)
        log.info('Stop job runners')
        jobRunners.each { jobRunner ->
            jobRunner.finish()
        }
        log.info('Join threads')
        threads.each { thread ->
            thread.join()
        }
        log.info('Scheduler is finished')
    }
}
