package com.example;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class RuntimePropertyChangeExample implements ApplicationListener<EnvironmentChangeEvent>{

	private Logger log = Logger.getLogger(RuntimePropertyChangeExample.class.getName());
	
	@Autowired
	private Environment env;
	
	@Autowired
	private TaskScheduler taskScheduler;
	
	private ScheduledFuture<?> future;
	
	@PostConstruct
	public void schedule()  {
    	future = taskScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("" + new Date() +  " test.property value is : " + env.getProperty("test.property"));
			}
		}, new CronTrigger(env.getProperty("test.property.cron")));
    	
	}
    
    @RequestMapping(path="/testProperty")
    public String testProperty(){
    	return env.getProperty("test.property");
    }

	@Override
	public void onApplicationEvent(EnvironmentChangeEvent arg0) {
		future.cancel(false);
		schedule();
	}

	
}
