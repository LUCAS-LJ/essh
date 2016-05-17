/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.modules.sys.entity.Dictionary;
import com.eryansky.modules.sys.service.DictionaryManager;
import com.eryansky.modules.sys.service.DictionaryTypeManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 数据字典Dictionary管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/dictionary")
public class DictionaryController extends BaseController<Dictionary,Long> {

    @Autowired
    private DictionaryManager dictionaryManager;
    @Autowired
    private DictionaryTypeManager dictionaryTypeManager;

    @Override
    public EntityManager<Dictionary, Long> getEntityManager() {
        return dictionaryManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/dictionary";
    }

    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model")Dictionary dictionary) {
        return "modules/sys/dictionary-input";
    }

    @Override
    public Datagrid<Dictionary> datagrid() {
        Page<Dictionary> page = new Page<Dictionary>(SpringMVCHolder.getRequest());
        // 自动构造属性过滤器
        List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(SpringMVCHolder.getRequest(), HibernateWebUtils.FILTERPREFIX, false);
        page = getEntityManager().findPage(page,filters);
        Datagrid<Dictionary> datagrid = new Datagrid<Dictionary>(page.getTotalCount(), page.getResult());
        return datagrid;
    }

    /**
     * 保存
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result _save(@ModelAttribute("model") Dictionary dictionary, String parentDictionaryCode) {
//        getEntityManager().evict(dictionary);
        Result result = null;
        // 名称是否重复校验
//        Dictionary checkDictionary1 = dictionaryManager.findUniqueBy(
//                "name", dictionary.getName());
//        if (checkDictionary1 != null
//                && !checkDictionary1.getId().equals(dictionary.getId())) {
//            result = new Result(Result.WARN, "名称为[" + dictionary.getName()
//                    + "]已存在,请修正!", "name");
//            logger.debug(result.toString());
//            return result;
//        }

        // 编码是否重复校验
        Dictionary checkDictionary2 = dictionaryManager.getByCode(dictionary
                .getCode());
        if (checkDictionary2 != null
                && !checkDictionary2.getId().equals(dictionary.getId())) {
            result = new Result(Result.WARN, "编码为[" + dictionary.getCode()
                    + "]已存在,请修正!", "code");
            logger.debug(result.toString());
            return result;
        }

        // 设置上级节点
        if (!StringUtils.isEmpty(parentDictionaryCode)) {
            Dictionary parentDictionary = dictionaryManager.getByCode(parentDictionaryCode);
            if (parentDictionary == null) {
                logger.error("上级数据字典[{}]已被删除.", parentDictionaryCode);
                throw new ActionException("上级数据字典已被删除.");
            }
            dictionary.setParentDictionary(parentDictionary);
        } else {
            dictionary.setParentDictionary(null);
        }

        // 设置字典类型
        if (StringUtils.isNotBlank(dictionary.getDictionaryTypeCode())) {
            dictionary.setDictionaryType(dictionaryTypeManager.getByCode(dictionary
                    .getDictionaryTypeCode()));
        } else {
            logger.error("字典类型为空.");
            throw new ActionException("字典类型为空.");
        }

        dictionaryManager.saveEntity(dictionary);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    @Override
    public Result remove(@RequestParam(value = "ids", required = false) List<Long> ids) {
        dictionaryManager.deleteByIds(ids);
        return Result.successResult();
    }

    /**
     * combotree下拉列表数据.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"combotree"})
    @ResponseBody
    public List<TreeNode> combotree(@ModelAttribute("model") Dictionary dictionary, String selectType) throws Exception {
        List<TreeNode> titleList = Lists.newArrayList();
        // 添加 "---全部---"、"---请选择---"
        if (!StringUtils.isBlank(selectType)) {
            SelectType s = SelectType.getSelectTypeValue(selectType);
            if (s != null) {
                TreeNode selectTreeNode = new TreeNode("",
                        s.getDescription());
                titleList.add(selectTreeNode);
            }
        }
        List<TreeNode> treeNodes = dictionaryManager
                .getByDictionaryTypeCode(dictionary.getDictionaryTypeCode(), dictionary.getId(), true);

        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * combobox下拉列表框数据.
     *
     * @param selectType
     * @param dictionaryTypeCode 字典类型编码
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"combobox"})
    @ResponseBody
    public List<Combobox> combobox(String selectType, String dictionaryTypeCode) throws Exception {
        List<Combobox> titleList = Lists.newArrayList();
        // 为combobox添加 "---全部---"、"---请选择---"
        if (!StringUtils.isBlank(selectType)) {
            SelectType s = SelectType.getSelectTypeValue(selectType);
            if (s != null) {
                Combobox selectCombobox = new Combobox("",
                        s.getDescription());
                titleList.add(selectCombobox);
            }
        }

        List<Combobox> cList = dictionaryManager
                .getByDictionaryTypeCode(dictionaryTypeCode);
        List<Combobox> unionList = ListUtils.union(titleList, cList);
        return unionList;
    }

    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort() throws Exception {
        Integer maxSort = dictionaryManager.getMaxSort();
        Result result = new Result(Result.SUCCESS, null, maxSort);
        return result;
    }

}
