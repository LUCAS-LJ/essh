/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.service.DiskManager;
import com.eryansky.utils.AppConstants;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * 云盘公共接口 以及相关工具类
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-10
 */
public class DiskUtils {

	private static DiskManager diskManager = SpringContextHolder.getBean(DiskManager.class);

	/**
	 * 文件虚拟路径 用于文件转发
	 */
	public static final String FILE_VIRTUAL_PATH = "disk/file/";
	/**
	 * 文件夹标识 通知
	 */
	public static String FOLDER_NOTICE = "notice";
    /**
     * kindeditor
     */
    public static String FOLDER_KINDEDITOR = "kindeditor";
    /**
     * 用户头像
     */
    public static String FOLDER_USER_PHOTO = "userphoto";

    /**
     * 文件上传失败提示信息
     */
    public static final String UPLOAD_FAIL_MSG = "文件上传失败！";


    /**
     * KindEditor编辑器文件
     * @param userId
     * @return
     */
    public static String getKindEditorRelativePath(Long userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(FOLDER_KINDEDITOR);
        return getRelativePath(folder,userId);
    }


    /**
     * 得到用户头像出差相对路径
     * @param userId 用户ID
     * @return
     */
    public static String getUserPhotoRelativePath(Long userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(FOLDER_USER_PHOTO);
        return getRelativePath(folder,userId);
    }



    /**
     * 得到用户相对路径
     * @param folderCode 文件夹编码
     * @param userId 用户ID
     * @return
     */
    public static String getRelativePath(String folderCode,Long userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(folderCode);
        return getRelativePath(folder,userId);
    }


	/**
	 *  生成对象保存的相对地址
	 * 
	 * @param folder
	 *            文件夹
	 * @return
	 */
	public static String getRelativePath(Folder folder, Long userId) {
		Date now = Calendar.getInstance().getTime();
		StringBuffer path = new StringBuffer();
		path.append(DateFormatUtils.format(now, "yyyy"))
				.append(java.io.File.separator);
		String folderAuthorize = FolderAuthorize
				.getFolderAuthorize(folder.getFolderAuthorize()).toString()
				.toLowerCase();
		path.append(folderAuthorize).append(java.io.File.separator);
		if (FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize())) {
			path.append(folder.getUserId()).append(java.io.File.separator)
					.append(folder.getId());
		} else if (FolderAuthorize.Organ.getValue().equals(
				folder.getFolderAuthorize())) {
			path.append(folder.getOrganId()).append(java.io.File.separator)
					.append(folder.getId());
		} else if (FolderAuthorize.Role.getValue().equals(
				folder.getFolderAuthorize())) {
			path.append(folder.getRoleId()).append(java.io.File.separator)
					.append(folder.getId());
		} else if (FolderAuthorize.Pulic.getValue().equals(
				folder.getFolderAuthorize())) {
			path.append(folder.getId());
		} else if (FolderAuthorize.SysTem.getValue().equals(
				folder.getFolderAuthorize())) {
			path.append(folder.getCode());
            if (userId != null) {
                path.append(java.io.File.separator).append(userId);
            }
        }
		return path.toString();
	}
	
	/**
	 * 根据文件夹 创建基准目录
	 * 
	 * @param folder
	 *            文件夹
	 * @return
	 */
	public static String getFolderPath(Folder folder, Long userId) {
		StringBuffer path = new StringBuffer();
		String relativePath = getRelativePath(folder, userId);
		path.append(AppConstants.getDiskBasePath())
				.append(java.io.File.separator).append(relativePath);
				
		return path.toString();
	}
	

	/**
	 * 根据编码获取 获取系统文件夹 <br/>
	 * 如果不存在则自动创建
	 * 
	 * @param code
	 *            系统文件夹编码
	 * @return
	 */
	public static Folder getSystemFolderByCode(String code) {
		return diskManager.checkSystemFolderByCode(code);
	}

    /**
     * 根据编码、用户ID获取 获取系统文件夹 <br/>
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    public static Folder getSystemFolderByCode(String code,Long userId) {
        return diskManager.checkSystemFolderByCode(code,userId);
    }

    /**
     * 获取用户通知文件夹
     * @param userId
     * @return
     */
    public static Folder getUserNoticeFolder(Long userId) {
        return diskManager.checkSystemFolderByCode(FOLDER_NOTICE,userId);
    }

    /**
     * 获取用户头像文件夹
     * @param userId
     * @return
     */
    public static Folder getUserPhotoFolder(Long userId) {
        return diskManager.checkSystemFolderByCode(FOLDER_USER_PHOTO,userId);
    }

	/**
	 * 保存系统文件
	 * 
	 * @param folderCode
	 *            系统文件夹编码
	 * @param request
	 *            请求对象
	 * @param sessionInfo
	 *            session信息 允允许为null
	 * @param multipartFile
	 *            上传文件对象 SpringMVC
	 * @return
	 * @throws com.eryansky.core.web.upload.exception.InvalidExtensionException
	 * @throws org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException
	 * @throws com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException
	 * @throws java.io.IOException
	 */
	public static File saveSystemFile(String folderCode,
			HttpServletRequest request, SessionInfo sessionInfo,
			MultipartFile multipartFile) throws InvalidExtensionException,
			FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
		Long userId = null;
		if (sessionInfo != null && sessionInfo.getUserId() != null) {
			userId = sessionInfo.getUserId();
		}

		String code = FileUploadUtils.encodingFilenamePrefix(userId + "",
                multipartFile.getOriginalFilename());
		Folder folder = getSystemFolderByCode(folderCode, userId);
		String url = null; //附件路径
		if (request == null) {
			String relativePath = getRelativePath(folder, userId); // 除配置路径外的文件夹路径
			url = FileUploadUtils.upload(null, relativePath, multipartFile,
                    null, AppConstants.getDiskMaxUploadSize(), true, code);
		} else {
			String baseDir = getFolderPath(folder, userId);
			url = FileUploadUtils.upload(request, baseDir, multipartFile, null,
                    AppConstants.getDiskMaxUploadSize(), true, code);
		}

		File file = new File();
		file.setFolder(folder);
		file.setCode(code);
		file.setUserId(userId);
		file.setName(multipartFile.getOriginalFilename());
		file.setFilePath(url);
		file.setFileSize(multipartFile.getSize());
		file.setFileSuffix(FilenameUtils.getExtension(multipartFile
                .getOriginalFilename()));
		diskManager.saveFile(file);
		return file;
	}


	/**
	 * 保存通知文件
	 *
	 * @param request
	 * @param sessionInfo
	 * @param multipartFile
	 * @return
	 * @throws com.eryansky.core.web.upload.exception.InvalidExtensionException
	 * @throws org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException
	 * @throws com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException
	 * @throws java.io.IOException
	 */
	public static File saveNoticeFile(HttpServletRequest request,
                                       SessionInfo sessionInfo, MultipartFile multipartFile)
            throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        return saveSystemFile(DiskUtils.FOLDER_NOTICE, request, sessionInfo,
                multipartFile);
    }


    /**
     * 保存用户头像文件
     *
     * @param request
     * @param sessionInfo
     * @param multipartFile
     * @return
     * @throws com.eryansky.core.web.upload.exception.InvalidExtensionException
     * @throws org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException
     * @throws com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException
     * @throws java.io.IOException
     */
    public static File saveUserPhotoFile(HttpServletRequest request,
                                        SessionInfo sessionInfo, MultipartFile multipartFile)
            throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        return saveSystemFile(DiskUtils.FOLDER_USER_PHOTO, request, sessionInfo,
                multipartFile);
    }

    /**
     * 更新文件
     * @param file 文件
     * @return
     */
    public static void updateFile(File file){
        diskManager.updateFile(file);
    }


    /**
     * 删除文件
     * @param request
     * @param fileId 文件ID
     * @return
     */
    public static void deleteFile(HttpServletRequest request,Long fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        diskManager.deleteFile(request,fileId);
    }
    /**
     * 删除文件
     * @param file
     * @return
     */
    public static void deleteFile(HttpServletRequest request,File file){
        Validate.notNull(file, "参数[file]不能为null.");
        diskManager.deleteFile(request, file);
    }

	/**
	 * 获取文件虚拟路径
	 * @param file
	 * @return
	 */
	public static String getVirtualFilePath(File file){
		return AppConstants.getAdminPath() + "/" + FILE_VIRTUAL_PATH + file.getId();
	}
}
