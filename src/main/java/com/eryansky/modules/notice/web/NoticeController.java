/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.notice.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.modules.notice.utils.NoticeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.DiskManager;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice.entity.Notice;
import com.eryansky.modules.notice.entity.NoticeScope;
import com.eryansky.modules.notice.service.NoticeManager;
import com.eryansky.modules.notice.service.NoticeScopeManager;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.SelectType;
import com.eryansky.utils.YesOrNo;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 邮件管理
 */
@Controller
@RequestMapping(value = "${adminPath}/notice/notice")
public class NoticeController extends BaseController<Notice, Long> {

	@Autowired
	private NoticeManager noticeManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private NoticeScopeManager noticeScopeManager;
    @Autowired
    private DiskManager diskManager;
    @Autowired
    private FileManager fileManager;

    /**
     * 操作类型
     */
    public enum OperateType{
        Save,Publish,RePublish,Repeat//保存、发送、重新发布、转发
    }

    @Override
	public EntityManager<Notice, Long> getEntityManager() {
		return noticeManager;
	}

    /**
     * 通知发布（通知管理）
     * @param noticeId 通知ID
     * @return
     */
	@RequestMapping(value = { ""})
	public ModelAndView list(Long noticeId) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice");
        modelAndView.addObject("noticeId",noticeId);
		return modelAndView;
	}


    /**
     * 发布通知列表
     * @param noticeQueryVo {@link com.eryansky.modules.notice.vo.NoticeQueryVo} 查询条件
     * @return
     */
    @RequestMapping(value = { "noticeDatagrid" })
	@ResponseBody
	public String noticeDatagrid(NoticeQueryVo noticeQueryVo) {
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		Page<Notice> page = new Page<Notice>(SpringMVCHolder.getRequest());
		Long userId = sessionInfo.getUserId();// 发布人ID
		if (NoticeUtils.isNoticeAdmin(userId)) {
            userId = null;// 管理员 查询所有
		}
        noticeQueryVo.syncEndTime();
		page = noticeManager.findPage(page, userId,noticeQueryVo);
		Datagrid<Notice> dg = new Datagrid<Notice>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg);
		return json;
	}



    /**
     * 查看通知读取情况
     * @param id 通知ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "readInfo/{id}" })
    public ModelAndView readInfo(@PathVariable Long id)
            throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-readInfo");
        modelAndView.addObject("noticeId",id);
        return modelAndView;
    }

    /**
     * 通知阅读情况
     * @param id 通知ID
     * @return
     */
    @RequestMapping(value = { "readInfoDatagrid/{id}" })
    @ResponseBody
    public Datagrid<NoticeScope> readInfoDatagrid(@PathVariable Long id) {
        Page<NoticeScope> page = new Page<NoticeScope>(SpringMVCHolder.getRequest());
        page = noticeScopeManager.findReadInfoPage(page, id);
        Datagrid<NoticeScope> dg = new Datagrid<NoticeScope>(page.getTotalCount(), page.getResult());
        return dg;
    }


    /**
     * 查看通知
     * @param id 通知ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "view/{id}" })
    public ModelAndView view(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-view");
        List<com.eryansky.modules.disk.entity.File> files = null;
        Notice model = noticeManager.loadById(id);
        if(Collections3.isNotEmpty(model.getFileIds())){
            files = diskManager.findFilesByIds(model.getFileIds());
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        noticeScopeManager.setRead(sessionInfo.getUserId(),model.getId());
        modelAndView.addObject("files", files);
        modelAndView.addObject("model", model);
        return modelAndView;
    }


    /**
	 * @param notice
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "input" })
	public ModelAndView input(@ModelAttribute("model") Notice notice,OperateType operateType) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-input");
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		Long loginUserId = sessionInfo.getUserId();
		List<com.eryansky.modules.disk.entity.File> files = null;
		if (OperateType.Repeat.equals(operateType) ) {// 转发
			List<Long> newFileIds = Lists.newArrayList();
			if (Collections3.isNotEmpty(notice.getFileIds())) {// 文件拷贝
				List<File> sourceFiles = diskManager.findFilesByIds(notice.getFileIds());
				List<File> newFiles = new ArrayList<File>(sourceFiles.size());
				newFileIds = Lists.newArrayList();
				for (File sourceFile : sourceFiles) {
					File file = sourceFile.copy();
					file.setStatus(StatusState.lock.getValue());
					file.setFolder(DiskUtils.getUserNoticeFolder(loginUserId));
					file.setUserId(loginUserId);
					diskManager.saveFile(file);
					newFileIds.add(file.getId());
					newFiles.add(file);
				}

				files = newFiles;
			}
            notice = notice.repeat();
			notice.setFileIds(newFileIds);
		}
		if (Collections3.isNotEmpty(notice.getFileIds())) {
			files = diskManager.findFilesByIds(notice.getFileIds());
		}
        modelAndView.addObject("files", files);
        modelAndView.addObject("effectTime", DateUtils.format(notice.getEffectTime(), Notice.DATE_TIME_SHORT_FORMAT));
        modelAndView.addObject("operateType", operateType);
        modelAndView.addObject("model", notice);
		return modelAndView;
	}


    /**
     * 保存
     * @param notice
     * @param operateType {@link OperateType}
     * @param noticeUserIds
     * @param noticeOrganIds
     * @param fileIds 页面文件ID集合
     * @return
     */
	@RequestMapping(value = { "_save" })
	@ResponseBody
	public Result _save(
			@ModelAttribute("model") Notice notice,OperateType operateType,
			@RequestParam(value = "_noticeUserIds", required = false) List<Long> noticeUserIds,
			@RequestParam(value = "_noticeOrganIds", required = false) List<Long> noticeOrganIds,
            @RequestParam(value = "_fileIds", required = false)List<Long> fileIds) {
        getEntityManager().evict(notice);
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		Result result;

        //更新文件为有效状态 上传的时候为lock状态
        if(Collections3.isNotEmpty(fileIds)){
            List<File> noticeFiles = diskManager.findFilesByIds(fileIds);
            for(File noticeFile:noticeFiles){
                noticeFile.setStatus(StatusState.normal.getValue());
                diskManager.updateFile(noticeFile);
            }
        }

        List<Long> oldFileIds = null;//原有文件的ID
        if(notice.getId() != null){
            oldFileIds = noticeManager.getFileIds(notice.getId());//原有文件的ID
        }
        List<Long> newFileIds = fileIds;//当前文件的ID
        List<Long> removeFileIds =  Lists.newArrayList();//删除的文件ID
        if(Collections3.isEmpty(newFileIds)){
            removeFileIds = oldFileIds;
        }else{
            if(Collections3.isNotEmpty(oldFileIds)){
                for(Long oldFileId:oldFileIds){
                    if(!newFileIds.contains(oldFileId)){
                        removeFileIds.add(oldFileId);
                    }
                }
            }

        }
        //组件上移除文件
        if(Collections3.isNotEmpty(removeFileIds)){
            fileManager.deleteFolderFiles(null,removeFileIds);
        }
        notice.setFileIds(fileIds);


        if(YesOrNo.NO.getValue().equals(notice.getIsToAll())){
            notice.setNoticeUserIds(noticeUserIds);
            notice.setNoticeOrganIds(noticeOrganIds);
        }
        if(notice.getUserId() == null){
            notice.setUserId(sessionInfo.getUserId());
            notice.setOrganId(sessionInfo.getLoginOrganId());
        }

        boolean isPublish = false;
        if(OperateType.Publish.equals(operateType)){
            isPublish = true;
        }

        noticeManager.saveFromModel(notice,isPublish);
		result = Result.successResult();
		return result;
	}


    /**
     * 标记为已读
     * @param ids
     * @return
     */
    @RequestMapping(value = { "markReaded" })
    @ResponseBody
    public Result markReaded(
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        for (Long id : ids) {
            noticeScopeManager.setRead(sessionInfo.getUserId(),id);
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 发布通知
     * @param id 通知ID
     * @return
     */
    @RequestMapping(value = { "publish/{id}" })
    @ResponseBody
    public Result publish(@PathVariable Long id) {
        noticeManager.publish(id);
        return Result.successResult();
    }

    /**
     * 终止通知
     * @param id 通知ID
     * @return
     */
    @RequestMapping(value = { "invalid/{id}" })
    @ResponseBody
    public Result invalid(@PathVariable Long id) {
        Result result;
        Notice notice = getEntityManager().loadById(id);
        notice.setNoticeMode(NoticeMode.Invalidated.getValue());
        notice.setEndTime(Calendar.getInstance().getTime());
        getEntityManager().update(notice);
        result = Result.successResult();
        return result;
    }

    /**
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = { "_remove" })
    @ResponseBody
    public Result _remove(
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        Result result = null;
        if(Collections3.isNotEmpty(ids)){
            for(Long id:ids){
                noticeManager.removeNotice(id);
            }
        }
        result = Result.successResult();
        return result;
    }

	/**
	 * 文件上传
	 */
	@RequestMapping(value = { "upload" })
	@ResponseBody
	public static Result upload( @RequestParam(value = "uploadFile", required = false)MultipartFile multipartFile) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Exception exception = null;
        File file = null;
        try {
            file = DiskUtils.saveNoticeFile(null,sessionInfo,multipartFile);
            file.setStatus(StatusState.lock.getValue());
            DiskUtils.updateFile(file);
            result = Result.successResult().setObj(file.getId()).setMsg("文件上传成功！");
        } catch (InvalidExtensionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (ActionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (IOException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } finally {
            if (exception != null) {
                if(file != null){
                    DiskUtils.deleteFile(null,file.getId());
                }
            }
        }
        return result;

	}


    /**
     * 删除附件
     * @param notice
     * @param fileId
     * @return
     */
    @RequestMapping(value = { "delUpload" })
    @ResponseBody
    public Result delUpload(@ModelAttribute("model") Notice notice,@RequestParam Long fileId) {
        Result result = null;
        notice.getFileIds().remove(fileId);
        getEntityManager().saveEntity(notice);
        DiskUtils.deleteFile(null,fileId);

        result = Result.successResult();
        return result;
    }

    /**
     * 是否置顶 下拉列表
     * @param selectType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "isTopCombobox" })
    @ResponseBody
    public List<Combobox> IsTopCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        IsTop[] _emums = IsTop.values();
        for (IsTop column : _emums) {
            Combobox combobox = new Combobox(column.getValue().toString(),
                    column.getDescription());
            cList.add(combobox);
        }
        return cList;
    }



    /**
     * 我的通知
     * @return
     */
    @RequestMapping(value = { "read" })
    public ModelAndView readList() {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-read");
        return modelAndView;
    }

    /**
     * 我的通知
     * @param noticeQueryVo 查询条件
     * @return
     */
    @RequestMapping(value = { "readDatagrid" })
    @ResponseBody
    public String noticeReadDatagrid(NoticeQueryVo noticeQueryVo) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Page<NoticeScope> page = new Page<NoticeScope>(SpringMVCHolder.getRequest());
        noticeQueryVo.syncEndTime();
        page = noticeManager.findReadNoticePage(page, sessionInfo.getUserId(),noticeQueryVo);
        Datagrid<NoticeScope> dg = new Datagrid<NoticeScope>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,Notice.class,
                new String[]{"id","title","type","typeView","publishUserName","publishTime","isReadView"});
        return json;
    }

    /**
     * 通知数量
     * @return
     */
    @RequestMapping(value = { "myMessage"})
    @ResponseBody
    public Result myMessage(HttpServletResponse response){
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        long noticeScopes = noticeScopeManager.getUserUnreadNoticeNum(sessionInfo.getUserId());
        Map<String,Long> map = Maps.newHashMap();
        map.put("noticeScopes", noticeScopes);
        result = Result.successResult().setObj(map);
        return result;
    }

}
