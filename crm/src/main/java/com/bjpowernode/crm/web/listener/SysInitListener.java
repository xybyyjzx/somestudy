package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * 2019/10/10
 */
/*

    监听器都能够监听域对象的创建与销毁，域对象中属性值的添加，更新与移除
    需要监听什么，就需要实现什么接口
    例如我们现在要监听的是全局域对象（创建），那么我们就需要实现ServletContextListener

 */
public class SysInitListener implements ServletContextListener {

    /*

        该方法是用来监听application对象创建的方法
        当application对象被服务器创建完毕后，则立即执行该方法
        如果该方法执行了，说明application对象创建了

        参数event：可以通过该参数取得监听的域对象
                    例如我们现在要监听的是application对象
                    那么我们就可以通过event参数来取得application对象

     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

        ServletContext application = event.getServletContext();

        System.out.println("application对象创建了："+application);

        System.out.println("服务器缓存处理数据字典开始");

        //处理数据字典
        //创建数据字典的业务层对象
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        Map<String,List<DicValue>> map = ds.getAll();

        //将map中所有的7组键值对保存成为application中的键值对
        Set<String> set = map.keySet();
        for(String key:set){

            application.setAttribute(key, map.get(key));

        }
        System.out.println("服务器缓存处理数据字典结束");

        System.out.println("服务器缓存处理阶段和可能性之间的对应关系开始");

        /*

            将Stage2Possibility.properties中的键值对保存成为map
            将map保存到application中，以缓存的形式呈现出来

         */

        /*

            调用getBundle方法，由参数指定需要解析的properties属性文件的路径及文件名
            我们的属性文件都是存放在resources路径下，所以不需要指定，直接列出来文件名即可
            注意：只写文件名，后缀名一定要去除掉

         */
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Map<String,String> pMap = new HashMap<>();

        Enumeration<String> e = rb.getKeys();

        while(e.hasMoreElements()){

            //key
            String stage = e.nextElement();
            //value
            String possibility = rb.getString(stage);

            System.out.println("stage："+stage);
            System.out.println("possibility："+possibility);
            System.out.println("-----------------------------");

            pMap.put(stage, possibility);

        }
        application.setAttribute("pMap", pMap);
        f


    }
}































