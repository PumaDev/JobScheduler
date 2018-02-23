package org.dethstart.facefound.scheduler.service.job

import org.dethstart.facefound.scheduler.exception.JobNotFoundException
import org.springframework.stereotype.Service

import javax.annotation.Resource

@Service
class ScheduleJobServiceImpl implements ScheduleJobService {

    @Resource
    Map<String, ScheduleJob> jobs = [:]

    @Override
    ScheduleJob getScheduleJob(String jobType) {
        ScheduleJob scheduleJob = jobs[jobType]
        if (!scheduleJob) {
            throw new JobNotFoundException()
        }
        return scheduleJob
    }
}
