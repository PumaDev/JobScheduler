package org.dethstart.facefound.scheduler.service.job

import org.dethstart.facefound.scheduler.exception.JobNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Service
class ScheduleJobServiceImpl implements ScheduleJobService {

    Map<String, ScheduleJob> jobs = [:]

    @Autowired
    ApplicationContext applicationContext

    @PostConstruct
    void init() {
        jobs = applicationContext.getBeansOfType(ScheduleJob).collectEntries {
            [(it.value.getClass().simpleName): it.value]
        } as Map<String, ScheduleJob>
    }

    @Override
    ScheduleJob getScheduleJob(String jobType) {
        ScheduleJob scheduleJob = jobs[jobType]
        if (!scheduleJob) {
            throw new JobNotFoundException()
        }
        return scheduleJob
    }
}
