package com.manong.config.security.service;
import com.manong.entity.Permission;
import com.manong.entity.User;
import com.manong.service.PermissionService;
import com.manong.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerUserDetailsService implements UserDetailsService {
    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;
    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        //调用根据用户名查询用户信息的方法
        User user = userService.findUserByUserName(username);
        //如果对象为空，则认证失败
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误!");
        }
        //查询用户所拥有的权限列表
        List<Permission> permissionList= permissionService.findPermissionListByUserId(user.getId());
        //获取权限编码
        List<String> collect=permissionList.stream()
                .filter(item->item!=null)
                .map(item->item.getCode()).filter(item->item!=null)
                .collect(Collectors.toList());
        //转换成数组
        String[] strings=collect.toArray(new String[collect.size()]);
        //设置权限列表
        List<GrantedAuthority> authorities=AuthorityUtils.createAuthorityList(strings);
        user.setAuthorities(authorities);
        //设置菜单列表
        user.setPermissionList(permissionList);
        return user;
    }
}
