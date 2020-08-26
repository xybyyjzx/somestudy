package com.bjpowernode.crm.test.user;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 2019/9/30
 */
public class Test01 {

    public static void main(String[] args) {

        /*String pwd = "BJpowernode@qq.com";

        pwd = MD5Util.getMD5(pwd);

        System.out.println(pwd);*/

        String expireTime = "2019-09-10 10:10:10";

        /*Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentSysTime = sdf.format(date);*/

        String currentSysTime = DateTimeUtil.getSysTime();

        int count = expireTime.compareTo(currentSysTime);

        System.out.println(count);


    }

}
