/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.modules.sys.entity.Config;
import com.eryansky.modules.sys.service.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-05-14 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/config")
public class ConfigController extends BaseController<Config, Long> {
    @Autowired
    private ConfigManager configManager;

    @Override
    public EntityManager<Config, Long> getEntityManager() {
        return configManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/config";
    }

    @RequestMapping(value = {"input"})
    public String input() {
        return "modules/sys/config-input";
    }

    @Override
    public Datagrid<Config> datagrid() {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        List filters = HibernateWebUtils.buildPropertyFilters(request);
        Page p = new Page(request);
        p = this.getEntityManager().findPage(p, filters, false);
        Datagrid datagrid = new Datagrid(p.getTotalCount(), p.getResult());
        return datagrid;
    }


    @Override
    public Result save(@ModelAttribute("model") Config model) {
        getEntityManager().evict(model);
        Result result;
        // 属性名重复校验
        Config checkConfig = configManager.findUniqueBy("code", model.getCode());
        if (checkConfig != null && !checkConfig.getId().equals(model.getId())) {
            result = new Result(Result.WARN, "属性名为[" + model.getCode() + "]已存在,请修正!", "code");
            logger.debug(result.toString());
            return result;
        }

        configManager.saveEntity(model);
        result = Result.successResult();
        return result;
    }

    /**
     * 从配置文件同步
     * @param overrideFromProperties
     * @return
     */
    @RequestMapping(value = {"syncFromProperties"})
    @ResponseBody
    public Result syncFromProperties(Boolean overrideFromProperties){
        Result result;
        configManager.syncFromProperties(overrideFromProperties);
        result = Result.successResult();
        return result;
    }
}