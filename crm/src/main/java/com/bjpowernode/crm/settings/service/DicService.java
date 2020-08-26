package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

/**
 * 作者：动力节点老崔
 * 2019/10/10
 */
public interface DicService {
    Map<String, List<DicValue>> getAll();
}
