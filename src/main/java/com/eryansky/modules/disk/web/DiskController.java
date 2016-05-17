/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.DownloadUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.disk.service.FolderManager;
import com.eryansky.modules.disk.service.OrganStorageManager;
import com.eryansky.modules.disk.service.UserStorageManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 我的云盘 管理
 * 包含：文件夹的管理 文件的管理 以及文件分享
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-22
 */
@Controller
@RequestMapping(value = "${adminPath}/disk")
public class DiskController extends SimpleController {

    @Autowired
    private FolderManager folderManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private OrganManager organManager;
    @Autowired
    private UserStorageManager userStorageManager;
    @Autowired
    private OrganStorageManager organStorageManager;

    @RequestMapping(value = {""})
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk");
        return modelAndView;
    }


    /**
     * 文件夹编辑页面
     * @param folderId
     * @param folderAuthorize {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param parentFolderId
     * @param organId
     * @return
     */
    @RequestMapping(value = {"folderInput"})
    public ModelAndView folderInputPage(Long folderId,Integer folderAuthorize,Long parentFolderId,Long organId,Long roleId){
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk-folderInput");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Folder model = new Folder();
        if(folderId !=null){
            model = folderManager.loadById(folderId);
        }

        modelAndView.addObject("model",model);
        modelAndView.addObject("folderAuthorize",folderAuthorize);
        if(parentFolderId != null){//不允许在别人的文件夹下创建文件夹
            Folder parentFolder = folderManager.loadById(parentFolderId);
            if(!parentFolder.getUserId().equals(sessionInfo.getUserId())){
                parentFolderId = null;
            }
        }
        modelAndView.addObject("parentFolderId",parentFolderId);
        modelAndView.addObject("organId",organId);
        return modelAndView;
    }

    /**
     * 文件夹树
     * @param folderAuthorize {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param organId
     * @param excludeFolderId
     * @param selectType
     * @return
     */
    @RequestMapping(value = {"folderTree"})
    @ResponseBody
    public List<TreeNode> folderTree(Integer folderAuthorize,Long organId,Long excludeFolderId,String selectType){
        List<TreeNode> treeNodes = Lists.newArrayList();

        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            treeNodes.add(selectTreeNode);
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<TreeNode> folderTreeNodes = null;
        if(FolderAuthorize.Organ.getValue().equals(folderAuthorize) && organId == null){//部门网盘 没有传递organId返回null
            folderTreeNodes = Lists.newArrayList();
        }else{
            folderTreeNodes = folderManager.getFolders(folderAuthorize,sessionInfo.getUserId(),organId,excludeFolderId,true);
        }
        treeNodes.addAll(folderTreeNodes);
        return treeNodes;
    }

    /**
     * 保存文件夹
     * @return
     */
    @RequestMapping(value = {"folderAuthorizeCombobox"})
    @ResponseBody
    public List<Combobox> folderAuthorizeCombobox(String selectType){
        List<Combobox> cList = Lists.newArrayList();
        Combobox selectCombobox = SelectType.combobox(selectType);
        if(selectCombobox != null){
            cList.add(selectCombobox);
        }
//        if(SecurityUtils.isDiskAdmin(null)){
//            FolderAuthorize[] _enums = FolderAuthorize.values();
//            for(int i=0;i<_enums.length;i++){
//                Combobox combobox = new Combobox(_enums[i].getValue().toString(), _enums[i].getDescription());
//                cList.add(combobox);
//            }
//        }else{
//            Combobox combobox = new Combobox(FolderAuthorize.User.getValue().toString(), FolderAuthorize.User.getDescription());
//            cList.add(combobox);
//            combobox = new Combobox(FolderAuthorize.Organ.getValue().toString(),FolderAuthorize.Organ.getDescription());
//            cList.add(combobox);
//            combobox = new Combobox(FolderAuthorize.Pulic.getValue().toString(), FolderAuthorize.Pulic.getDescription());
//            cList.add(combobox);
//        }

        Combobox combobox = new Combobox(FolderAuthorize.User.getValue().toString(), FolderAuthorize.User.getDescription());
        cList.add(combobox);
        combobox = new Combobox(FolderAuthorize.Organ.getValue().toString(), FolderAuthorize.Organ.getDescription());
        cList.add(combobox);
        combobox = new Combobox(FolderAuthorize.Pulic.getValue().toString(), FolderAuthorize.Pulic.getDescription());
        cList.add(combobox);

        return cList;
    }

    public enum ModelType{
        Folder,File;
    }

    @ModelAttribute
    public void getModel(ModelType modelType,Long id, Model uiModel){
        if(modelType != null && id != null){
            if(modelType.equals(ModelType.Folder)){
                uiModel.addAttribute("model",folderManager.loadById(id));
            }else if(modelType.equals(ModelType.File)){
                uiModel.addAttribute("model",fileManager.loadById(id));
            }
        }

    }


    /**
     * 保存文件夹
     * @return
     */
    @RequestMapping(value = {"saveFolder"})
    @ResponseBody
    public Result saveFolder(@ModelAttribute("model")Folder folder){
        Result result = null;
        if(folder.getUserId() == null){
            folder.setUserId(SecurityUtils.getCurrentSessionInfo().getUserId());
        }
        folderManager.saveOrUpdate(folder);
        result = Result.successResult();
        return result;
    }

    /**
     * 删除文件夹
     * @param folderId 文件夹ID
     * @return
     */
    @RequestMapping(value = {"folderRemove/{folderId}"})
    @ResponseBody
    public Result folderRemove(@PathVariable Long folderId){
        Result result = null;
        folderManager.deleteFolderAndFiles(folderId);
        result = Result.successResult();
        return result;
    }


    /**
     * 磁盘树 节点类型
     */
    public enum NType{
        FolderAuthorize,Folder,Organ;
    }
    public static final String NODE_TYPE = "nType";
    public static final String NODE_OPERATE = "operate";
    public static final String NODE_USERNAME = "userName";


    /**
     * 是否允操作文件夹
     * @param folderId 文件夹ID
     * @param isAdmin 是否是管理员
     * @return
     */
    private boolean isOperateFolder(Long folderId,boolean isAdmin){
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Folder folder = folderManager.loadById(folderId);
        boolean operateAble = isAdmin;
        if(!operateAble && folder != null){
            if(sessionInfo.getUserId().equals(folder.getUserId())){
                operateAble = true;
            }
        }
        return operateAble;
    }

    /**
     * 递归用户文件夹树
     * @param userTreeNodes
     * @param folder
     * @param isCascade
     */
    public void recursiveUserFolderTreeNode(List<TreeNode> userTreeNodes, Folder folder, boolean isCascade){
        TreeNode treeNode = new TreeNode(folder.getId().toString(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE, DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_OPERATE,true);
        treeNode.setIconCls("eu-icon-folder");
        userTreeNodes.add(treeNode);
        if(isCascade){
            List<Folder> childFolders = folderManager.getChildFoldersByByParentFolderId(folder.getId());
            List<TreeNode> childTreeNodes = Lists.newArrayList();
            for(Folder childFolder:childFolders){
                this.recursiveUserFolderTreeNode(childTreeNodes, childFolder, isCascade);
            }
            for(TreeNode childTreeNode:childTreeNodes){
                treeNode.addChild(childTreeNode);
            }
        }

    }

    /**
     * 递归部门文件夹树
     * @param organTreeNode
     * @param isAdmin
     */
    private void recursiveOrganTreeNode(TreeNode organTreeNode,boolean isAdmin){
        organTreeNode.getAttributes().put(NODE_TYPE, NType.Organ.toString());
        organTreeNode.setState(TreeNode.STATE_OPEN);
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        long limitStorage = organStorageManager.getOrganAvaiableStorage(sessionInfo.getLoginOrganId());//部门可用空间
        long usedStorage = fileManager.getOrganUsedStorage(Long.valueOf(organTreeNode.getId()));//部门已用空间
        String organNodeName = organTreeNode.getText();
        if(OrganType.department.getValue().equals(organTreeNode.getAttributes().get("type"))){
            organNodeName = organTreeNode.getText()+"("+ PrettyMemoryUtils.prettyByteSize(usedStorage)+"/"+ PrettyMemoryUtils.prettyByteSize(limitStorage)+")";
        }
        organTreeNode.setText(organNodeName);


        //用户在部门下的文件夹
        List<Folder> organUserFolders = folderManager.getOrganFolders(Long.valueOf(organTreeNode.getId()), sessionInfo.getUserId(), false, null);
        //排除用户在部门以外的所有文件夹
        List<Folder> excludeUserOrganFolders = folderManager.getOrganFolders(Long.valueOf(organTreeNode.getId()), sessionInfo.getUserId(), true, null);
        //合并
        List<Folder> organFolders = Collections3.aggregate(organUserFolders, excludeUserOrganFolders);

        List<TreeNode> treeNodes = Lists.newArrayList();
        Iterator<Folder> iterator = organFolders.iterator();
        while (iterator.hasNext()){
            Folder folder = iterator.next();
            folderManager.recursiveFolderTreeNode(treeNodes,folder,null,true);
        }
        for(int i=0;i<treeNodes.size();i++){
            TreeNode t = treeNodes.get(i);
            Folder folder = organFolders.get(i);
            t.getAttributes().put(DiskController.NODE_OPERATE,this.isOperateFolder(folder.getId(),isAdmin));
            t.setText(t.getText()+"（<span style='color:blue;'>"+organFolders.get(i).getUserName()+"</span>）");
            organTreeNode.addChild(t);
        }



        for(TreeNode childTreeNode:organTreeNode.getChildren()){
            if(!NType.Folder.toString().equals((String) childTreeNode.getAttributes().get(NODE_TYPE))){
                recursiveOrganTreeNode(childTreeNode,isAdmin);
            }
        }
    }

    /**
     * 递归用户文件夹树
     * @param publicTreeNodes
     * @param folder
     * @param isAdmin
     */
    public void recursivePublicFolderTreeNode(List<TreeNode> publicTreeNodes, Folder folder,boolean isAdmin){
        TreeNode treeNode = new TreeNode(folder.getId().toString(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE, DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_OPERATE,this.isOperateFolder(folder.getId(),isAdmin));
        treeNode.setIconCls("eu-icon-folder");
        publicTreeNodes.add(treeNode);
        List<Folder> childFolders = folderManager.getChildFoldersByByParentFolderId(folder.getId());
        List<TreeNode> childTreeNodes = Lists.newArrayList();
        for(Folder childFolder:childFolders){
            this.recursivePublicFolderTreeNode(childTreeNodes, childFolder, isAdmin);
        }
        for(TreeNode childTreeNode:childTreeNodes){
            treeNode.addChild(childTreeNode);
        }

    }
    /**
     * 个人 磁盘树
     *
     * @return
     */
    @RequestMapping(value = {"diskTree"})
    @ResponseBody
    public List<TreeNode> diskTree(){
        List<TreeNode> treeNodes = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<Folder> userFolders = folderManager.getAuthorizeFoldersByUserId(sessionInfo.getUserId());
        List<Folder> userOwnerFolders = Lists.newArrayList();
        List<Folder> publicFolders = Lists.newArrayList();
        List<Folder> userAuthorizeOrganFolders = Lists.newArrayList();
        List<Folder> roleFolders = Lists.newArrayList();
        //文件夹数据分类
        for(Folder folder:userFolders){
            if(FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize()) && sessionInfo.getUserId().equals(folder.getUserId())){
                userOwnerFolders.add(folder);
            }else if(FolderAuthorize.Organ.getValue().equals(folder.getFolderAuthorize())){
                userAuthorizeOrganFolders.add(folder);
            }else if(FolderAuthorize.Pulic.getValue().equals(folder.getFolderAuthorize())){
                publicFolders.add(folder);
            }else if(FolderAuthorize.Role.getValue().equals(folder.getFolderAuthorize())){
                roleFolders.add(folder);
            }
        }

        //树构造
        //个人
        long limitStorage = userStorageManager.getUserAvaiableStorage(sessionInfo.getUserId());//用户可用个人空间
        long usedStorage = fileManager.getUserUsedStorage(sessionInfo.getUserId());//用户已用空间
        TreeNode userOwnerTreeNode = new TreeNode(FolderAuthorize.User.getValue().toString(),
                FolderAuthorize.User.getDescription()+"("+ PrettyMemoryUtils.prettyByteSize(usedStorage)+"/"+ PrettyMemoryUtils.prettyByteSize(limitStorage)+")");
        userOwnerTreeNode.getAttributes().put(NODE_TYPE, NType.FolderAuthorize.toString());
        userOwnerTreeNode.setIconCls("eu-icon-user");

        List<Folder> folders = folderManager.getFoldersByFolderAuthorize(FolderAuthorize.User.getValue(),sessionInfo.getUserId(),null,null,null);
        List<TreeNode> userFolderTreeNodes = Lists.newArrayList();
        for(Folder folder:folders){
            this.recursiveUserFolderTreeNode(userFolderTreeNodes, folder, true);
        }

        for(TreeNode userFolderTreeNode:userFolderTreeNodes){
            userOwnerTreeNode.addChild(userFolderTreeNode);
        }
        treeNodes.add(userOwnerTreeNode);

        //部门
        TreeNode organTreeNode = new TreeNode(FolderAuthorize.Organ.getValue().toString(), FolderAuthorize.Organ.getDescription());
        organTreeNode.getAttributes().put(NODE_TYPE, NType.FolderAuthorize.toString());
        organTreeNode.setIconCls("eu-icon-disk_organ");

        Long organId = sessionInfo.getLoginOrganId();
        boolean isAdmin = SecurityUtils.isDiskAdmin(null);
        if(isAdmin){
            organId = null;
        }
        List<TreeNode> organTreeNodes = organManager.getOrganTree(organId, null, true, false,null);
        for(TreeNode organNode:organTreeNodes){
            organNode.setState(TreeNode.STATE_CLOASED);
            this.recursiveOrganTreeNode(organNode,isAdmin);
            organTreeNode.addChild(organNode);
        }
        treeNodes.add(organTreeNode);

        //公共
        TreeNode publicTreeNode = new TreeNode(FolderAuthorize.Pulic.getValue().toString(), FolderAuthorize.Pulic.getDescription());
        publicTreeNode.getAttributes().put(NODE_TYPE, NType.FolderAuthorize.toString());
        publicTreeNode.setIconCls("eu-icon-disk_public");
        List<TreeNode> publicTreeNodes = folderManager.getFolders(FolderAuthorize.Pulic.getValue(),null,null,null,true);
//        for(Folder folder:publicFolders){
//            recursivePublicFolderTreeNode(publicTreeNodes,folder,isAdmin);
//        }
        for(TreeNode treeNode:publicTreeNodes){
            treeNode.getAttributes().put(DiskController.NODE_OPERATE,this.isOperateFolder(Long.valueOf(treeNode.getId()) ,isAdmin));
            treeNode.setText(treeNode.getText()+"（<span style='color:blue;'>"+treeNode.getAttributes().get(NODE_USERNAME)+"</span>）");
            publicTreeNode.addChild(treeNode);
        }
        treeNodes.add(publicTreeNode);

        //角色 TODO

        return treeNodes;
    }


    /**
     * 文件列表
     * @param folderId 文件夹Id
     * @return
     */
    @RequestMapping(value = {"folderFileDatagrid"})
    @ResponseBody
    public String folderFileDatagrid(Long folderId,String fileName){
        Page<File> page = new Page<File>(SpringMVCHolder.getRequest());
        if(folderId == null){
            return JsonMapper.getInstance().toJson(new Datagrid());
        }
        page = fileManager.findPage(page,folderId,fileName);
        Datagrid<File> dg = new Datagrid<File>(page.getTotalCount(),page.getResult());
        //fotter
        List<Map<String,Object>> footer = Lists.newArrayList();
        long totalSize = 0L;//分页总大小
        if(Collections3.isNotEmpty(page.getResult())){
            for(File file:page.getResult()){
                totalSize += file.getFileSize();
            }
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("name","总大小");
        map.put("prettyFileSize", PrettyMemoryUtils.prettyByteSize(totalSize));
        footer.add(map);
        dg.setFooter(footer);

        String json = JsonMapper.getInstance().toJson(dg,File.class,
                new String[]{"id","name","prettyFileSize","createTime","userName"});
        return json;
    }


    /**
     * 文件上传页面
     * @param folderId 文件夹ID
     * @return
     */
    @RequestMapping(value = {"fileInput"})
    public ModelAndView fileInputPage(Long folderId){
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk-fileInput");
        Folder model = new Folder();
        if(folderId !=null){
            model = folderManager.loadById(folderId);
            if(model != null){
                modelAndView.addObject("folderName",model.getName());
            }
        }
        modelAndView.addObject("folderId",folderId);
        return modelAndView;
    }


    /**
     * 文件信息修改
     * @return
     */
    @RequestMapping(value = {"fileSave"})
    @ResponseBody
    public Result fileSave(@ModelAttribute("model")File file){
        fileManager.saveEntity(file);
        return Result.successResult();
    }


    /**
     * 上传容量校验
     * @param sessionInfo
     * @param folder
     * @param uploadFileSize
     * @return
     * @throws com.eryansky.common.exception.ActionException
     */
    private boolean checkStorage(SessionInfo sessionInfo,Folder folder,long uploadFileSize) throws ActionException {
        boolean flag = false;
        if(FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize())){
            long limitStorage = userStorageManager.getUserAvaiableStorage(sessionInfo.getUserId());//用户可用个人空间
            long usedStorage = fileManager.getUserUsedStorage(sessionInfo.getUserId());//用户已用空间
            long avaiableStorage = limitStorage - usedStorage;
            if(avaiableStorage < uploadFileSize){
                throw new ActionException("用户个人云盘空间不够！可用大小："+ PrettyMemoryUtils.prettyByteSize(avaiableStorage));
            }
        }else if(FolderAuthorize.Organ.getValue().equals(folder.getFolderAuthorize())){
            long limitStorage = organStorageManager.getOrganAvaiableStorage(sessionInfo.getLoginOrganId());//部门可用个人空间
            long usedStorage = fileManager.getOrganUsedStorage(folder.getOrganId());//部门已用空间
            long avaiableStorage = limitStorage - usedStorage;
            if(avaiableStorage < uploadFileSize){
                throw new ActionException("部门云盘空间不够！可用大小："+ PrettyMemoryUtils.prettyByteSize(avaiableStorage));
            }
        }
        return  flag;
    }

    /**
     * 文件上传容量校验
     * @param folderId 文件夹ID
     * @param uploadFileSize 上传文件的大小 单位：字节
     * @return
     */
    @RequestMapping(value = {"fileLimitCheck/{folderId}"})
    @ResponseBody
    public Result fileLimitCheck(@PathVariable Long folderId,Long uploadFileSize,String filename) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result = null;
        try {
            Folder folder = folderManager.loadById(folderId);
            checkStorage(sessionInfo,folder,uploadFileSize);
            result = Result.successResult();
        } catch (ActionException e){
            result = Result.errorResult().setMsg("文件【"+filename+"】上传失败，"+e.getMessage());
        }
        return result;
    }

    /**
     * 文件上传
     * @param folderId 文件夹
     * @param uploadFile 上传文件
     * @return
     */
    @RequestMapping(value = {"fileUpload"})
    @ResponseBody
    public Result fileUpload( @RequestParam(value = "folderId", required = false)Long folderId,
                             @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) throws IOException {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result = null;
        String url = null;

        Folder folder = folderManager.loadById(folderId);
        String relativeDir = DiskUtils.getRelativePath(folder,sessionInfo.getUserId());
        File file = null;
        Exception exception = null;
        try {
            checkStorage(sessionInfo,folder,uploadFile.getSize());
            String code = FileUploadUtils.encodingFilenamePrefix(sessionInfo.getUserId().toString(), uploadFile.getOriginalFilename());
            url = FileUploadUtils.upload(null, relativeDir, uploadFile, null, AppConstants.getDiskMaxUploadSize(), true, code);
            file = new File();
            file.setFolder(folder);
            file.setCode(code);
            file.setUserId(sessionInfo.getUserId());
            file.setName(uploadFile.getOriginalFilename());
            file.setFilePath(url);
            file.setFileSize(uploadFile.getSize());
            file.setFileSuffix(FilenameUtils.getExtension(uploadFile.getOriginalFilename()));
            fileManager.save(file);

            Map<String,String> obj = Maps.newHashMap();
            obj.put("fileId",file.getId().toString());
            obj.put("name", file.getName());
            obj.put("filePath", file.getFilePath());
            result = Result.successResult().setObj(obj).setMsg("文件上传成功！");
        } catch (InvalidExtensionException e) {
//            e.printStackTrace();
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG+e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
//            e.printStackTrace();
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
//            e.printStackTrace();
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (ActionException e){
//            e.printStackTrace();
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG+e.getMessage());
        }finally {
            if (exception != null) {
                if(file != null){
                    fileManager.delete(file);
                }
            }
        }
        return  result;

    }

    /**
     * 文件转发 根据文件ID查找到服务器上的硬盘文件 virtual
     * @param response
     * @param request
     * @param fileId
     */
    @RequestMapping(value = { "file/{fileId}" })
    public void file(HttpServletResponse response,
                     HttpServletRequest request, @PathVariable Long fileId) {
        File file = fileManager.loadById(fileId);
        try {
            java.io.File diskFile = null;
            if(file != null){
                diskFile = file.getDiskFile();
            }
            FileCopyUtils.copy(new FileInputStream(diskFile), response.getOutputStream());
            response.setHeader("Content-Type", "application/octet-stream");
            return;
        } catch (FileNotFoundException e) {
            if(logger.isWarnEnabled()) {
                logger.warn(String.format("请求的文件%s不存在", fileId), e.getMessage());
            }
            return;
        } catch (IOException e) {
            logger.warn(String.format("请求的文件%s不存在", fileId), e.getMessage());
        }

    }


    @RequestMapping(value = {"fileDownload/{fileId}"})
    public void fileDownload(HttpServletResponse response,
                             HttpServletRequest request,
                             @PathVariable Long fileId) {
        File file = fileManager.loadById(fileId);

        //TODO 文件下载权限校验
        ActionException fileNotFoldException = new ActionException("文件不存在，已被删除或移除。");
        if(file == null){
            logger.error("文件[{}]不存在",new Object[]{fileId});
            throw fileNotFoldException;
        }

        try {
            String fileBasePath = FileUploadUtils.getBasePath(file.getFilePath());
            file.getDiskFile();
            java.io.File diskFile  = FileUploadUtils.getAbsoluteFile(fileBasePath);
            if(!diskFile.exists() || !diskFile.canRead()){
                throw fileNotFoldException;
            }
            String displayName = file.getName();
            DownloadUtils.download(request, response, new FileInputStream(diskFile), displayName);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw fileNotFoldException;
        }

    }


    /**
     * 文件删除
     * @param fileIds 文件集合
     * @return
     */
	@RequestMapping(value = { "delFolderFile" })
	@ResponseBody
	public Result delFolderFile(
			@RequestParam(value = "fileIds", required = false) List<Long> fileIds) {
		fileManager.deleteFolderFiles(null, fileIds);
		return Result.successResult();
	}


}