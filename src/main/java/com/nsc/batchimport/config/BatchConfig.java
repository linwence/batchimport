package com.nsc.batchimport.config;

import com.nsc.batchimport.listener.StringJobListener;
import com.nsc.batchimport.processor.StringItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.job.JobStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import(DruidDBConfig.class)
public class BatchConfig {
    private Logger logger= LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    public ItemReader<String>  reader(){
        // 使用FlatFileItemReader去读cvs文件，一行即一条数据
        FlatFileItemReader<String> reader=new FlatFileItemReader();
        // 设置文件处在路径
        reader.setResource(new ClassPathResource(""));

//        reader.setLineMapper(new DefaultLineMapper<String>(){
//            {
//                setLineTokenizer();
//            }
//
//        });
        return  reader;

    }

    /**
     * 注册ItemProcessor: 处理数据+校验数据
     * @return
     */
    @Bean
    public ItemProcessor<String, String> processor(){
        StringItemProcessor stringItemProcessor = new StringItemProcessor();
        // 设置校验器
       // stringItemProcessor.setValidator(stringBeanValidator());
        return stringItemProcessor;
    }

    /**
     * ItemWriter定义：指定datasource，设置批量插入sql语句，写入数据库
     * @param dataSource
     * @return
     */
    @Bean
    public ItemWriter<String> writer(DataSource dataSource){
        // 使用jdbcBcatchItemWrite写数据到数据库中
        JdbcBatchItemWriter<String> writer=new JdbcBatchItemWriter<>();
        // 设置有参数的sql语句
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<String>());
        String sql=" ";

        writer.setSql(sql);
        writer.setDataSource(dataSource);
        return writer;
    }


    /**
     * JobRepository定义：设置数据库，注册Job容器
     * @param dataSource
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public JobRepository stringJobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception{


        JobRepositoryFactoryBean jobRepositoryFactoryBean=new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDatabaseType("Microsoft SQL Server");
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        return jobRepositoryFactoryBean.getObject();
    }

    /**
     * jobLauncher定义：
     * @param dataSource
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public SimpleJobLauncher  sqlJobLauncher(DataSource dataSource,PlatformTransactionManager transactionManager) throws Exception{
        SimpleJobLauncher simpleJobLauncher=new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(stringJobRepository(dataSource,transactionManager));
        return  simpleJobLauncher;
    }

    @Bean
    public Job importJob(JobBuilderFactory jobBuilderFactory, Step step){
        return jobBuilderFactory.get("importCsvJob")
                .incrementer(new RunIdIncrementer()).flow(step).end().listener(stringJobListener()).build();

    }

    @Bean
    public StringJobListener stringJobListener(){
        return new StringJobListener();
    }

    /**
     * step定义：步骤包括ItemReader->ItemProcessor->ItemWriter 即读取数据->处理校验数据->写入数据
     * @param stepBuilderFactory
     * @param reader
     * @param writer
     * @param processor
     * @return
     */
    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<String> reader, ItemWriter<String> writer, ItemProcessor<String,String> processor){

        return stepBuilderFactory.get("step").<String,String>chunk(50000).reader(reader).processor(processor).writer(writer).build();
    }




}
