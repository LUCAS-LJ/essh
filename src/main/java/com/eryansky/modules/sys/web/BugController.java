/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.excel.ExcelUtil;
import com.eryansky.common.excel.ExportExcel;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.modules.sys.entity.Bug;
import com.eryansky.modules.sys.entity.Dictionary;
import com.eryansky.modules.sys.service.BugManager;
import com.eryansky.modules.sys.service.DictionaryManager;
import com.eryansky.modules.sys.utils.DictionaryUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * bug管理Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-27 下午8:02:39
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/bug")
public class BugController extends BaseController<Bug,Long> {

    public final static String SSSION_SEARCH = "BUG_SEARCH";

    @Autowired
    private BugManager bugManager;
    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public EntityManager<Bug, Long> getEntityManager() {
        return bugManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/bug";
    }

    @RequestMapping(value = {"input"})
    public String input() {
        return "modules/sys/bug-input";
    }

    @RequestMapping(value = {"view"})
    public String view(@ModelAttribute("model") Bug bug) {
        return "modules/sys/bug-view";
    }


    /**
     * 初始化数据绑定
     * @param binder
     */
    @Override
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        Object annotationValue = AnnotationUtils.getValue(AnnotationUtils.findAnnotation(entityClass, JsonIgnoreProperties.class));
        if (annotationValue != null) {
            String[] jsonIgnoreProperties = (String[]) annotationValue;
            binder.setDisallowedFields(jsonIgnoreProperties);
        }

        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Bug bug) {
        Result result;
        // 名称重复校验
        Bug checkBug = bugManager.findUniqueBy("title", bug.getTitle());
        if (checkBug != null && !checkBug.getId().equals(bug.getId())) {
            result = new Result(Result.WARN, "标题为[" + bug.getTitle() + "]已存在,请修正!", "title");
            return result;
        }

        bugManager.saveEntity(bug);
        result = Result.successResult();
        return result;
    }

    @Override
    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public Datagrid datagrid() {
        // 自动构造属性过滤器
        List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(SpringMVCHolder.getRequest());
        Page<Bug> p = new Page<Bug>(SpringMVCHolder.getRequest());
        p = getEntityManager().findPage(p,filters);

        //转换设置bug类型名称
        if (p.getResult() != null) {
            for (Bug bug : p.getResult()) {
//                Dictionary dictionary = dictionaryManager.getByCode(bug.getType());
                Dictionary dictionary = dictionaryManager.getDictionaryByDV(DictionaryUtils.DIC_BUG,bug.getType());
                if (dictionary != null) {
                    bug.setTypeName(dictionary.getName());
                } else {
                    logger.warn("[{}]未设置类型.", bug.getTitle());
                }
            }
        }
        Datagrid<Bug> dg = new Datagrid<Bug>(p.getTotalCount(), p.getResult());
        return dg;
    }

    @RequestMapping(value = {"import"})
    public String _import() {
        return "modules/sys/bug-import";
    }
    /**
     * Excel导入
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"importExcel"})
    @ResponseBody
    public Result importExcel(@RequestParam(value = "filedata")MultipartFile file){
        Result result = null;
        List<Bug> bugs = null;
        List<Bug> bugs_new = Lists.newArrayList();
        try {
            if (file != null) {
                bugs = (List<Bug>) ExcelUtil.importExcelByIs(file.getInputStream(), Bug.class);
                if (bugs != null && bugs.size() > 0) {
                    for (Bug bug : bugs) {
                        //重复数据校验
                        Bug checkBug = bugManager.findUniqueBy("title", bug.getTitle());
                        if (checkBug == null) {
                            bug.setVersion(0);
//                            bug.setContent(ClobUtil.getClob(bug.getContentView()));
                            Dictionary dictionary = dictionaryManager.findUniqueBy("name", bug.getTypeName());
                            if (dictionary != null) {
                                bug.setType(dictionary.getCode());
                            } else {
                                logger.warn("无法识别[{}].", bug.getTypeName());
                            }
                            bugs_new.add(bug);
                        } else {
                            logger.warn("[{}]已存在.", bug.getTitle());
                        }
                    }
                }
                bugManager.saveOrUpdate(bugs_new);
                result = new Result(Result.SUCCESS, "已导入" + bugs_new.size() + "条数据.", null);

            } else {
                result = new Result(Result.WARN, "未上传任何文件.", null);
            }

        } catch (IOException e) {
            logger.error("文件导入失败 "+e.getMessage(),e);
            result = new Result(Result.ERROR, "文件导入失败", null);
        } catch (Exception e) {
            logger.error("文件格式不正确，导入失败 "+e.getMessage(),e);
            result = new Result(Result.ERROR, "文件格式不正确，导入失败", null);
        } finally {
            return result;
        }
    }

    /**
     * Excel导出
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"exportExcel"})
    public void exportExcel(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
        // 生成提示信息，
        final String fileName = "内容信息.xls";
        OutputStream outStream = null;
        try {
            //设置文件类型
            response.setContentType(WebUtils.EXCEL_TYPE);
            //设置下载弹出对话框
            WebUtils.setDownloadableHeader(request, response, fileName);
            //从session中获取查询参数
            List<PropertyFilter> sessionFilters = (List<PropertyFilter>) session.getAttribute(SSSION_SEARCH);
            List<Bug> bugs = null;
            if (sessionFilters != null) {
                bugs = bugManager.find(sessionFilters, "orderNo", Page.ASC);
            } else {
                bugs = bugManager.getAll("id", Page.ASC);
            }
            //设置bug类型（此处由于Bug未直接关联Dictionary所以被动设置类型名称）
            for (Bug bug : bugs) {
                String dicStringName = "";
                if(StringUtils.isNotBlank(bug.getType())){
                    dicStringName = DictionaryUtils.getDictionaryNameByDV(DictionaryUtils.DIC_BUG,bug.getType(),null);
                    bug.setTypeName(dicStringName);
                }

            }
            HSSFWorkbook workbook = new ExportExcel<Bug>().exportExcel("导出信息",
                    Bug.class, bugs);
            outStream = response.getOutputStream();
            workbook.write(outStream);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException e) {

            }
        }
    }
}
