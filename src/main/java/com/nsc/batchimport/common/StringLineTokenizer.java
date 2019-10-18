package com.nsc.batchimport.common;

import org.springframework.batch.item.file.transform.AbstractLineTokenizer;

import java.util.List;

public class StringLineTokenizer extends AbstractLineTokenizer {
    @Override
    protected List<String> doTokenize(String s) {
        return null;
    }
}
