# Spring Batch File Reader

Spring Batch Reference Links:
> [Total Reference Documentaiton](https://docs.spring.io/spring-batch/docs/current/reference/html/index.html)
> 
> [Spring Batch Schema Stuff](https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html)
> 
> [Baeldung GitHub Spring Batch Examples](https://github.com/eugenp/tutorials/tree/master/spring-batch)
> 
> [Spring IO Configuring and Running a Job](https://docs.spring.io/spring-batch/docs/current/reference/html/job.html)
> 
> [Examples of StepScope I](https://www.programcreek.com/java-api-examples/?api=org.springframework.batch.core.configuration.annotation.StepScope)
> 
> [Examples of StepScope II](https://www.programcreek.com/java-api-examples/?code=michaelhoffmantech%2Fpatient-batch-loader%2Fpatient-batch-loader-master%2Fsrc%2Fmain%2Fjava%2Fcom%2Fpluralsight%2Fspringbatch%2Fpatientbatchloader%2Fconfig%2FBatchJobConfiguration.java)

## Run Spring Boot application
```
mvn spring-boot:run
```
## Comma delimited values
```
SP - ATA - AID - BPOA - EPOA - A - MAIN - SUB - Admin Bureau - GWA TAS - Agency Name - BETC - BETC Name - Effective Date - Suspend Date - IsCredit - Adj BETC - STAR TAS - STAR Dep Reg - STAR Dep Xfer - STAR Main Acct - Txn Type - Acct Type - Fund Type - Fund Type Description
```

### Docker Composer Up Command
```
docker-compose up -d
docker-compose -p "spring-batch-demo-shedlock" up -d
```