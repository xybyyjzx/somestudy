package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 2019/9/29
 */
public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户模块控制器");

        //url-pattern
        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){

            login(request,response);

        }else if("/settings/user/xxx.do".equals(path)){

            //xxx(request,response);

        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {


        System.out.println("进入到验证登录的操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        //将密码解析为MD5加密后的密文
        loginPwd = MD5Util.getMD5(loginPwd);

        /*

            如果你访问的是一个本机的ip，如果你使用localhost，则不会展现正确的ip地址
            可以使用127.0.0.1来代替本机的ip地址

         */
        String ip = request.getRemoteAddr();

        //创建业务层对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        User user = null;
        try {

            user = us.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user", user);


            //登录成功
            /*

                需要为前端提供：
                    {"success":true}

             */

            PrintJson.printJsonFlag(response, true);

        } catch (LoginException e) {

            e.printStackTrace();

            //登录失败
            /*

                需要为用户提供
                {"success":false,"msg":?}

             */

            //取得业务层抛出的异常信息
            String msg = e.getMessage();

            Map<String,Object> map = new HashMap<>();
            map.put("success", false);
            map.put("msg",msg);

            PrintJson.printJsonObj(response, map);


        }



    }
}
































