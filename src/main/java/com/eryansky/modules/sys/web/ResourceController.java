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
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源权限Resource管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/resource")
public class ResourceController extends BaseController<Resource,Long> {

    @Autowired
    private ResourceManager resourceManager;

    @Override
    public EntityManager<Resource, Long> getEntityManager() {
        return resourceManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/resource";
    }


    /**
     * 删除.
     */
    @RequestMapping(value = {"_delete/{id}"})
    @ResponseBody
    public Result _delete(@PathVariable Long id) {
        Result result;
        if(id != null){
            List<Long> ids = Lists.newArrayList();
            ids.add(id);
            resourceManager.deleteByIds(ids);
            result = Result.successResult();
        }else{
            result = new Result().setCode(Result.WARN).setMsg("参数[id]为空.");
        }

        if(logger.isDebugEnabled()){
            logger.debug(result.toString());
        }
        return result;
    }

    /**
     * @param resource
     * @param parentType 上级资源类型
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model") Resource resource, Integer parentType, Model model) throws Exception {
        if (parentType == null && resource.getParentResource() != null) {
            parentType = resource.getParentResource().getType();
        }
        model.addAttribute("parentType", parentType);
        return "modules/sys/resource-input";
    }

    @RequestMapping(value = {"treegrid"})
    @ResponseBody
    public Datagrid<Resource> treegrid(String sort, String order) throws Exception {
        List<PropertyFilter> filters = Lists.newArrayList();
        List<Resource> list = getEntityManager().find(filters, sort, order);
        Datagrid<Resource> dg = new Datagrid<Resource>(list.size(), list);
        return dg;
    }


    /**
     * 保存.
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Resource resource,Long _parentId)  {
        getEntityManager().evict(resource);
        Result result = null;
        resource.setParentResource(null);

        // 设置上级节点
        if (_parentId != null) {
            Resource parentResource = resourceManager.loadById(_parentId);
            if (parentResource == null) {
                logger.error("父级资源[{}]已被删除.", _parentId);
                throw new ActionException("父级资源已被删除.");
            }
            resource.setParentResource(parentResource);
        }

        if (resource.getId() != null) {
            if (resource.getId().equals(resource.get_parentId())) {
                result = new Result(Result.ERROR, "[上级资源]不能与[资源名称]相同.",
                        null);
                logger.debug(result.toString());
                return result;
            }
        }
        resourceManager.saveEntity(resource);
        result = Result.successResult();
        return result;
    }

    /**
     * 资源树.
     */
    @RequestMapping(value = {"tree"})
    @ResponseBody
    public List<TreeNode> tree(String selectType) throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
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
        treeNodes = resourceManager.getResourceTree(null, true);
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * 资源类型下拉列表.
     */
    @RequestMapping(value = {"resourceTypeCombobox"})
    @ResponseBody
    public List<Combobox> resourceTypeCombobox(String selectType, Integer parentType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        //为combobox添加  "---全部---"、"---请选择---"
        if (!StringUtils.isBlank(selectType)) {
            SelectType s = SelectType.getSelectTypeValue(selectType);
            if (s != null) {
                Combobox selectCombobox = new Combobox("", s.getDescription());
                cList.add(selectCombobox);
            }
        }

        ResourceType parentResourceType = ResourceType.getResourceType(parentType);
        if (parentResourceType != null) {
            if (parentResourceType.equals(ResourceType.menu)) {
                ResourceType[] rss = ResourceType.values();
                for (int i = 0; i < rss.length; i++) {
                    Combobox combobox = new Combobox();
                    combobox.setValue(rss[i].getValue().toString());
                    combobox.setText(rss[i].getDescription());
                    cList.add(combobox);
                }
            } else if (parentResourceType.equals(ResourceType.function)) {
                Combobox combobox = new Combobox();
                combobox.setValue(ResourceType.function.getValue().toString());
                combobox.setText(ResourceType.function.getDescription());
                cList.add(combobox);
            }
        } else {
            ResourceType[] rss = ResourceType.values();
            for (int i = 0; i < rss.length; i++) {
                Combobox combobox = new Combobox();
                combobox.setValue(rss[i].getValue().toString());
                combobox.setText(rss[i].getDescription());
                cList.add(combobox);
            }
        }

        return cList;
    }

    /**
     * 父级资源下拉列表.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"parentResource"})
    @ResponseBody
    public List<TreeNode> parentResource(@ModelAttribute("model") Resource resource, String selectType) {
        List<TreeNode> treeNodes = null;
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
        treeNodes = resourceManager.getResourceTree(resource.getId(), true);
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort() throws Exception {
        Result result;
        Integer maxSort = resourceManager.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        logger.debug(result.toString());
        return result;
    }

}
