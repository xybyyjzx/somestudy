package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.*;
import com.bjpowernode.crm.workbench.service.impl.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2019/9/29
 */
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到交易模块控制器");

        //url-pattern
        String path = request.getServletPath();

        if("/workbench/transaction/add.do".equals(path)){

            add(request,response);

        }else if("/workbench/transaction/getCustomerNameListByName.do".equals(path)){

            getCustomerNameListByName(request,response);

        }else if("/workbench/transaction/getContactsListByName.do".equals(path)){

            getContactsListByName(request,response);

        }else if("/workbench/transaction/getPossibilityByStage.do".equals(path)){

            getPossibilityByStage(request,response);

        }else if("/workbench/transaction/save.do".equals(path)){

            save(request,response);

        }else if("/workbench/transaction/detail.do".equals(path)){

            detail(request,response);

        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(path)){

            getHistoryListByTranId(request,response);

        }
    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据交易id取得关联的交易历史列表");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);

        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");

        //处理可能性
        for(TranHistory th:thList){

            //取得历史中的每一个阶段
            String stage = th.getStage();
            //通过阶段取得可能性
            String possibility = pMap.get(stage);
            //将可能性封装到th对象中
            th.setPossibility(possibility);

        }


        PrintJson.printJsonObj(response, thList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("跳转到交易详细信息页");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        //通过阶段取得可能性
        String stage = t.getStage();
        //阶段和可能性之间的对应关系
        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);

        t.setPossibility(possibility);

        request.setAttribute("t", t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);


    }

    private void save(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("执行交易的添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");   //注意：客户信息我们现在只有name，没有id
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setNextContactTime(nextContactTime);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if(flag){

            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }

    }

    private void getPossibilityByStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据阶段取得可能性");

        //取得阶段
        String stage = request.getParameter("stage");

        //取得阶段和可能性之间的对应关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");

        //取得可能性
        String possibility = pMap.get(stage);

        Map<String,String> map = new HashMap<>();
        map.put("possibility", possibility);

        PrintJson.printJsonObj(response, map);

    }

    private void getContactsListByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据联系人名称搜索联系人列表");

        String fullname = request.getParameter("fullname");

        ContactsService cs = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        List<Contacts> cList = cs.getContactsListByName(fullname);

        for(Contacts con:cList){

            String email = con.getEmail();

            if(email==null){

                email = "-";
                con.setEmail(email);

            }

            String mphone = con.getMphone();
            if(mphone==null){

                mphone = "-";
                con.setMphone(mphone);

            }


        }

        PrintJson.printJsonObj(response, cList);

    }

    private void getCustomerNameListByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据客户名称关键字，取得客户名称列表");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerNameListByName(name);

        PrintJson.printJsonObj(response, sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("跳转到交易添加页");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);

    }


}
































