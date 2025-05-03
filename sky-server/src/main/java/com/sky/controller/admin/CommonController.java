package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api("公共接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 阿里云OSS文件上传
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("开始上传文件: {}", file.getOriginalFilename());
        if(file.isEmpty()){
            return Result.error("文件不能为空");
        }
        // 生成唯一的文件名
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            // 上传文件到阿里云OSS
            String url = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(url);
        } catch (Exception e) {
            log.info("上传文件失败: {}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
