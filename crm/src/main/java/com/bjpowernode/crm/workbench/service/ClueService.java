package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：动力节点老崔
 * 2019/10/10
 */
public interface ClueService {
    boolean save(Clue c);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String clueId, String[] activityIds);

    boolean convert(String clueId, Tran t, String createBy);
}
