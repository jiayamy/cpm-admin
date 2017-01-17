package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.User;

public interface UserDao extends GenericDao<User, Long> {
	/**
	 * 用户列表
	 * @return
	 */
	Page<User> getUserPage(User user, Pageable pageable);
}
