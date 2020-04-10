package com.zcf.controller;


import com.zcf.mapper.GameServiceMapper;
import com.zcf.pojo.GameService;
import com.zcf.util.Body;
import com.zcf.util.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@RequestMapping("/gameService")
public class GameServiceController {

    @Autowired
    GameServiceMapper gm;
    @Autowired
    FileService iFileService;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取客服信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("get")
    public Body get(){
        GameService gameService = gm.selectById(1);
        return Body.newInstance(gameService);
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:修改客服信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("update")
    public Body update(GameService service,@RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request){
        if(file!=null){
            String path = request.getSession().getServletContext().getRealPath("service");
            String targetFileName = iFileService.upload(file,path);
            targetFileName = "/service/"+targetFileName;
            service.setImg(targetFileName);
            service.updateById();
            return Body.BODY_200;
        }else{
            service.updateById();
            return Body.BODY_200;
        }
    }
}

