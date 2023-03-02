package io.basc.framework.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.TriggerFiredBundle;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanFactoryAware;

/**
 * 依赖BeanFactory的实现
 * @see PropertySettingJobFactory
 * @see SimpleJobFactory
 * @author wcnnkh
 *
 */
public class BeanFactoryJobFactory extends PropertySettingJobFactory implements BeanFactoryAware{
	private BeanFactory beanFactory;
	
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler)
			throws SchedulerException {
		Job job;
		if(beanFactory != null){
			JobDetail jobDetail = bundle.getJobDetail();
	        Class<? extends Job> jobClass = jobDetail.getJobClass();
			if(!beanFactory.isInstance(jobClass)){
				throw new SchedulerException("Not supported " + jobClass);
			}
			
			job = beanFactory.getInstance(jobClass);
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.putAll(scheduler.getContext());
			jobDataMap.putAll(bundle.getJobDetail().getJobDataMap());
			jobDataMap.putAll(bundle.getTrigger().getJobDataMap());
			setBeanProps(job, jobDataMap);
		}else{
			job = super.newJob(bundle, scheduler);
		}
		return job;
	}
}
