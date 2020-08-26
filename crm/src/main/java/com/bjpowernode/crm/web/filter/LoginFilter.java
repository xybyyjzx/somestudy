package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 2019/10/8
 */
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入到判断有没有登录过的过滤器");

        /*

            ServletRequest和ServletResponse是父亲
            HttpServletRequest和HttpServletResponse是儿子

         */

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        /*

            判断session中的user对象
            如果user对象不为null，说明登录过
                                    将请求放行到目标资源

            如果user对象为null，说明没登录过
                                    重定向到登录页

            登录操作相关的请求应该自动放行
            登录页：/login.jsp
            验证登录请求：/settings/user/login.do

         */

        //取得请求路径
        String path = request.getServletPath();
        //如果是这两个请求，应该自动放行，不需要判断
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){

            chain.doFilter(req, resp);

        //其他所有请求，必须验证是否登录过
        }else{

            User user = (User)request.getSession().getAttribute("user");

            if(user!=null){

                //登录过
                chain.doFilter(req, resp);

            }else{

                //没登录过
            /*

                不论是转发还是重定向，在开发过程中，路径都是使用绝对路径
                传统绝对路径的写法：/项目名/具体资源路径

                转发：
                    转发路径的写法虽然使用的也是绝对路径，但是写法比较特殊，前面不加/项目名，这种路径也被称之为项目中的内部路径
                    /login.jsp

                重定向：
                    重定向路径的写法，就是传统的绝对路径的写法，前面必须加/项目名
                    /crm/login.jsp

                实际项目开发中，一定要将项目名写活
                request.getContextPath()：取得当前项目的 /项目名

             */
                response.sendRedirect(request.getContextPath()+"/login.jsp");

            }


        }


    }
}
