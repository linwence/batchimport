package com.nsc.batchimport.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

public class StringItemProcessor extends ValidatingItemProcessor<String> {
    private Logger logger = LoggerFactory.getLogger(StringItemProcessor.class);

    @Override
    public String process(String item) throws ValidationException {
        return super.process(item);
    }
}
