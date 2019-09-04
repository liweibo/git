/**
  * Copyright 2019 bejson.com 
  */
package com.example.zhuanchu.bean.pojo15;
import java.util.List;

/**
 * Auto-generated: 2019-08-15 12:41:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Item {

    private String name;
    private List<CMDitem> CMDitem;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setCMDitem(List<CMDitem> CMDitem) {
         this.CMDitem = CMDitem;
     }
     public List<CMDitem> getCMDitem() {
         return CMDitem;
     }

}