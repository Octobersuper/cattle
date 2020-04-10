package com.zcf.controller;


import com.zcf.mapper.BannerMapper;
import com.zcf.pojo.Banner;
import com.zcf.util.Body;
import com.zcf.util.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
@CrossOrigin
@RestController
@RequestMapping("/banner")
public class BannerController {
    @Autowired
    BannerMapper bm;
    @Autowired
    FileService iFileService;

    @GetMapping("getBanner")
    public Body getBanner(){
        return Body.newInstance(bm.selectById(1));
    }

    @PostMapping("update")
    public Body update(@RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request){

        if(file!=null){
            String path = request.getSession().getServletContext().getRealPath("banner");
            String targetFileName = iFileService.upload(file,path);
            targetFileName = "/banner/"+targetFileName;

            Banner banner = new Banner();
            banner.setId(1L);
            banner.setImg(targetFileName);
            banner.updateById();
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}

