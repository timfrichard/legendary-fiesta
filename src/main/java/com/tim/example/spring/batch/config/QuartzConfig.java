package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.properties.FileUploadJobProperties;
import com.tim.example.spring.batch.quartz.CustomSpringBeanJobFactory;
import com.tim.example.spring.batch.quartz.FileUploadQuartzJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    private final DataSource dataSource;

    private final FileUploadJobProperties fileUploadJobProperties;

    public QuartzConfig(final DataSource dataSource, final FileUploadJobProperties fileUploadJobProperties) {
        this.dataSource = dataSource;
        this.fileUploadJobProperties = fileUploadJobProperties;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }


    @Bean
    public JobDetail fileUploadJobDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", Constants.JOB_NAME);

        return JobBuilder.newJob(FileUploadQuartzJob.class).withIdentity(Constants.JOB_NAME).setJobData(jobDataMap).storeDurably().build();
    }

    @Bean
    public Trigger fileUploadTrigger(final JobDetail fileUploadJobDetail) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(fileUploadJobProperties.getIntervalInSeconds()).repeatForever();

        return TriggerBuilder.newTrigger().forJob(fileUploadJobDetail).withIdentity("fileUploadTrigger ").withSchedule(scheduleBuilder).build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(final Trigger jobOneTrigger,
                                                     final JobDetail jobOneDetail,
                                                     final SpringBeanJobFactory springBeanJobFactory) throws IOException {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(jobOneTrigger);
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(jobOneDetail);
        scheduler.setDataSource(dataSource);
        scheduler.setJobFactory(springBeanJobFactory);

        return scheduler;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(final ApplicationContext applicationContext) {
        CustomSpringBeanJobFactory jobFactory = new CustomSpringBeanJobFactory();

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
