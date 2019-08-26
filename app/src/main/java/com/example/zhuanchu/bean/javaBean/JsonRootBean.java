/**
  * Copyright 2019 bejson.com 
  */
package com.example.zhuanchu.bean.javaBean;
import java.util.List;

/**
 * Auto-generated: 2019-08-10 21:37:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private long log_id;
    private int words_result_num;
    private List<Words_result> words_result;
    public void setLog_id(long log_id) {
         this.log_id = log_id;
     }
     public long getLog_id() {
         return log_id;
     }

    public void setWords_result_num(int words_result_num) {
         this.words_result_num = words_result_num;
     }
     public int getWords_result_num() {
         return words_result_num;
     }

    public void setWords_result(List<Words_result> words_result) {
         this.words_result = words_result;
     }
     public List<Words_result> getWords_result() {
         return words_result;
     }

}