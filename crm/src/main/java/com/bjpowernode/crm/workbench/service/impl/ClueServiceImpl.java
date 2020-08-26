package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 2019/10/10
 */
public class ClueServiceImpl implements ClueService {

    //线索模块相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户模块相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人模块相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue c) {

        boolean flag = true;

        int count = clueDao.save(c);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    @Override
    public Clue detail(String id) {

        Clue c = clueDao.detail(id);

        return c;
    }

    @Override
    public boolean unbund(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.delete(id);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    @Override
    public boolean bund(String clueId, String[] activityIds) {

        boolean flag = true;

        //遍历所有的市场活动id
        for(String activityId:activityIds){

            //让每一个activityId和clueId做关联
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(activityId);

            //执行关联关系表的添加操作
            int count = clueActivityRelationDao.save(car);
            if(count!=1){

                flag = false;

            }

        }

        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {

        /*

            开始执行线索转换的业务
            做线索专业的业务需要使用到几乎所有的核心业务相关的表
            所以在处理业务逻辑之前，我们需要将所用到的表的dao全创建出来

         */
        boolean flag = true;
        /*

            1.根据线索id，根据id查单条，将需要转换的线索的记录查询到
                我们得到了这个线索的对象之后
                将该对象中的与公司相关的信息，抽取出来，生成客户
                将该对象中的与人相关的信息，抽取出来，生成联系人

         */

        Clue c = clueDao.getById(clueId);

        /*

            2.根据上述查询出来的c对象，取得公司名称
                c.getCompany()
              根据这个公司名称到客户表中进行查询，看看有没有这个客户
              如果有这个客户，则将这个客户查询出来，将来的业务流程会使用到客户的id
              如果没有这个客户，则根据该公司名称，新建一个客户，将来的业务流程会使用到客户的id

              注意：查询客户的过程，是根据名称进行精确匹配，而不是模糊匹配


         */

        String company = c.getCompany();

        Customer cus = customerDao.getByName(company);

        //判断cus是否为null，决定是否需要新建客户
        if(cus==null){

            //添加客户
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            cus.setAddress(c.getAddress());

            int count1 = customerDao.save(cus);
            if(count1!=1){
                flag = false;
            }


        }

        /*

            经过了第二步之后，不论我们查询出来的是原有的客户，还是建立的新客户
            总之，我们有客户对象cus了
            将来需要使用客户的id的时候，直接从cus对象中get取值就可以了

         */

        /*

            3.通过c对象抽取出来人相关的信息，添加一条联系人
                注意：联系人与客户不一样，不需要判断

         */

        //添加联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(DateTimeUtil.getSysTime());
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());

        int count2 = contactsDao.save(con);
        if(count2!=1){
            flag = false;
        }

        /*

            4.将线索关联的备注信息，备份（转换）到客户的备注以及联系人的备注中去

         */
        //查询出线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for(ClueRemark clueRemark:clueRemarkList){

            //取出需要备份的备注信息
            String noteContent = clueRemark.getNoteContent();

            //生成客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(DateTimeUtil.getSysTime());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(cus.getId());
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3!=1){
                flag = false;
            }

            //生成联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(con.getId());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4!=1){
                flag = false;
            }


        }

        /*

            5.将线索和市场活动的关联关系，备份到联系人和市场活动的关联关系中去

         */
        //查询出与该线索关联的 线索市场活动关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){

            //从每一条关联关系中，取出与线索关联的市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //将原来与线索关联的所有的市场活动id，现在与联系人建立新的关联关系
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                flag = false;
            }
        }

        /*

            6.如果有创建交易的需求，则为客户创建一笔交易

         */
        //通过判断t对象，是否为null，决定是否需要创建交易
        if(t!=null){

            /*

                在创建交易之前，controller已经为t对象封装了一些重要的属性值
                已经封装过的信息有：
                    id,name,money,expectedDate,stage,activityId,createBy,createTime
                其他的值：
                    我们可以通过以上已经完成的业务生成的对象中取值

             */

            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
            int count6 = tranDao.save(t);
            if(count6!=1){
                flag = false;
            }

            //7.如果以上我们创建了交易，则为该交易创建一条交易阶段历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(DateTimeUtil.getSysTime());
            th.setTranId(t.getId());
            th.setStage(t.getStage());
            th.setMoney(t.getMoney());
            th.setExpectedDate(t.getExpectedDate());
            int count7 = tranHistoryDao.save(th);
            if(count7!=1){
                flag = false;
            }

        }

        //8.删除线索关联的备注
        for(ClueRemark clueRemark:clueRemarkList){

            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                flag = false;
            }

        }

        //9.删除线索和市场活动的关联关系
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){

            int count9 = clueActivityRelationDao.delete(clueActivityRelation.getId());
            if(count9!=1){
                flag = false;
            }

        }

        //10.删除线索
        int count10 = clueDao.delete(clueId);
        if(count10!=1){
            flag = false;
        }


        return flag;
    }


}






















































