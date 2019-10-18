package com.nsc.batchimport.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * @description
 * 监听Job执行情况，则定义一个类实现JobExecutorListener，并定义Job的Bean上绑定该监听器
 */
public class StringJobListener implements JobExecutionListener {

    private Logger logger= LoggerFactory.getLogger(StringJobListener.class);
    private long startTime;
    private long endTime;
    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime=System.currentTimeMillis();
        logger.info("job process start...");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
endTime=System.currentTimeMillis();
        logger.info("job process end...");
        logger.info("cost time: " + (endTime - startTime) + "ms");
    }
}
