package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.User;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);
    
    Optional<User> findOneBySerialNum(String serialNum);

    @Query(value = "select distinct user from User user left join fetch user.authorities",
        countQuery = "select count(user) from User user")
    Page<User> findAllWithAuthorities(Pageable pageable);
    
    @Query(value="select user,deptInfo.name from User user,DeptInfo deptInfo where deptInfo.id = user.deptId and user.login = ?1")
    List<Object[]> findDetailByLogin(String login);
    
    @Query("from User u where u.activated = ?1 order by id asc")
	List<User> findAllByActivated(Boolean activated);
    
    @Query("select a,b,c from User a,DeptInfo b,DeptType c where a.deptId = b.id and b.type = c.id and a.login = ?1")
	List<Object[]> findUserInfoByLogin(String login);
}
