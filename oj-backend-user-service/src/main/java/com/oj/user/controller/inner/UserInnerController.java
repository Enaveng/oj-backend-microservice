package com.oj.user.controller.inner;


import com.oj.model.entity.User;
import com.oj.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 仅仅提供给服务内部调用的接口 不需要暴露给前端
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController {

    @Resource
    private UserService userService;

    /**
     * 根据id列表获取用户列表数据
     *
     * @param idList
     * @return
     */
    @GetMapping("/get/ids")
    public List<User> getByIdList(@RequestParam("idList") Collection<Long> idList) {
        List<User> users = userService.listByIds(idList);
        return users;
    }


    /**
     * 根据id获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }


}
