package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.service.ContactsService;

import java.util.List;

/**
 * 2019/10/12
 */
public class ContactsServiceImpl implements ContactsService {

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    @Override
    public List<Contacts> getContactsListByName(String fullname) {

        List<Contacts> cList = contactsDao.getContactsListByName(fullname);

        return cList;
    }
}




























