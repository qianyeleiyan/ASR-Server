package org.codesdream.asr.component.datamanager;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

@Component
public class DescriptionGenerator {

    public String getString(List<String> stringList){
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？']";

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for(String tag : stringList){
            if(count++ != 0) builder.append(",");
            tag = Pattern.compile(regEx).matcher(tag).replaceAll("").trim();
            builder.append(tag);
        }

        return builder.toString();
    }

    public List<String> getStringList(String string){
        List<String> stringList = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(string, ",");
        while(tokenizer.hasMoreTokens()){
            stringList.add(tokenizer.nextToken());
        }
        return stringList;
    }
}
