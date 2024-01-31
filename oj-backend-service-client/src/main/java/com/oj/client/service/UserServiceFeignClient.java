package com.oj.client.service;


import com.oj.common.common.ErrorCode;
import com.oj.common.constant.UserConstant;
import com.oj.common.exception.BusinessException;
import com.oj.model.entity.User;
import com.oj.model.enums.UserRoleEnum;
import com.oj.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

//用户服务远程调用接口
@FeignClient(name = "oj-backend-user-service", path = "/api/user")
public interface UserServiceFeignClient {

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    //对于传递的像request这类的参数不好定义 我们直接采用默认方法实现该接口
    default User getLoginUser(HttpServletRequest request) {
        //删除与数据库交互的操作
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    default boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 根据id列表获取用户列表
     */
    @GetMapping("/inner/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 根据id获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/inner/get/id")
    User getById(@RequestParam("userId") long userId);
}


