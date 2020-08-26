package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Contacts;

import java.util.List;

/**
 * 作者：动力节点老崔
 * 2019/10/12
 */
public interface ContactsService {
    List<Contacts> getContactsListByName(String fullname);
}
