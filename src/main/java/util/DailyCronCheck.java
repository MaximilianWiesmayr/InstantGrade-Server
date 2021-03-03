package util;

//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;

public class DailyCronCheck {
/*    public void runCron(){
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            Scheduler scheduler = sf.getScheduler();
            JobDetail checkJob = JobBuilder.newJob(DeletionJob.class).withIdentity("emptyTrash", "group1").build();
            Trigger deleteTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("deleteTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?"))
                    .build();
            scheduler.start();

            scheduler.scheduleJob(checkJob, deleteTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    } */
}
