package org.codesdream.asr.service;

import javafx.util.Pair;
import org.codesdream.asr.model.user.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 用户服务接口
 */
public interface IUserService {

   /**
    * 获得一个空的默认用户（已激活）
    * @return 用户对象
    */
   User getDefaultUser();

   /**
    * 获得列出所有用户
    * @return 用户对象列表
    */
   List<User> findAll();

   User newTestUser(String openid);

   /**
    * 通过用户ID号（数据库）查找用户对象
    * @param id 用户ID号
    * @return 用户对象
    */
   Optional<User> findUserById(int id);

   /**
    * 通过openid查找用户对象
    * @param openid 用户openid
    * @return
    */
   Optional<User> findUserByOpenid(String openid);

   /**
    * 查询用户是否存在
    * @param openid 用户openid
    * @return 用户存在状态标记与openid对应的用户对象（如果存在）
    */
   public Pair<Boolean, User> checkIfUserExists(String openid);

   /**
    * 获得用户所有的权限角色
    * @param user 用户对象
    * @return 用户权限列表
    */
   Collection<? extends GrantedAuthority> getUserAuthorities(User user);

   /**
    * 更新用户的密码（过程中自动计算密码散列值）
    * @param user 用户对象
    * @param password 用户密码
    * @return 用户对象（更新后）
    */
   User updatePassword(User user, String password);

   /**
    * 封禁用户
    * @param user 用户对象
    * @return 用户对象（更新后）
    */
   User disableUser(User user);

   /**
    * 通过用户ID号列表（数据库）获得用户对象列表
    * @param usersId 用户ID号列表
    * @return 用户对象列表
    */
   Set<User> findUsersById(Set<Integer> usersId);

   /**
    * 随机生成一个用户名
    * @param user 用户对象
    */
   void generateRandomUsername(User user);

   /**
    * 在数据库中保存一个新的用户对象
    * @param user 用户对象
    * @return 用户对象（更新后）
    */
   User save(User user);

   /**
    * 更新一个已有的用户对象在数据库中的信息
    * @param user 用户对象
    * @return 用户对象（更新后）
    */
   User update(User user);

   /**
    * 删除用一个在数据库中已有的用户
    *
    * @param user 用户对象
    */
   void delete(User user);

   /**
    * 为持久化之后的用户创建任务池和计划池
    *
    * @param user
    */
   void createPools(User user);


}
