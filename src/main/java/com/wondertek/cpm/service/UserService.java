package com.wondertek.cpm.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.Authority;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.UserBaseVo;
import com.wondertek.cpm.repository.AuthorityRepository;
import com.wondertek.cpm.repository.PersistentTokenRepository;
import com.wondertek.cpm.repository.UserDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.UserSearchRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.util.RandomUtil;
import com.wondertek.cpm.web.rest.vm.ManagedUserVM;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;
    @Inject
    private UserDao userDao;
    
    @Inject
    private UserSearchRepository userSearchRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userSearchRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                return user.getResetDate().isAfter(oneDayAgo);
           })
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(ZonedDateTime.now());
                return user;
            });
    }

    public User createUser(String login, String password, String firstName, String lastName, String email,
        String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        userSearchRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(ManagedUserVM managedUserVM) {
        User user = new User();
        user.setLogin(managedUserVM.getLogin());
        user.setFirstName(managedUserVM.getFirstName());
        user.setLastName(managedUserVM.getLastName());
        user.setEmail(managedUserVM.getEmail());
        if (managedUserVM.getLangKey() == null) {
            user.setLangKey("zh-cn"); // default language
        } else {
            user.setLangKey(managedUserVM.getLangKey());
        }
        if (managedUserVM.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            managedUserVM.getAuthorities().forEach(
                authority -> authorities.add(authorityRepository.findOne(authority))
            );
            user.setAuthorities(authorities);
        }
        String password = managedUserVM.getPassword();
        if(StringUtil.isNullStr(managedUserVM.getPassword())){
        	password = RandomUtil.generatePassword();
        }
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);
        
        user.setSerialNum(managedUserVM.getSerialNum());
        user.setDeptId(managedUserVM.getDeptId());
        user.setIsManager(managedUserVM.getIsManager());
        user.setDuty(managedUserVM.getDuty());
        user.setGrade(managedUserVM.getGrade());
        user.setGender(managedUserVM.getGender());
        user.setBirthYear(managedUserVM.getBirthYear());
        user.setBirthDay(managedUserVM.getBirthDay());
        user.setTelephone(managedUserVM.getTelephone());
        user.setWorkArea(managedUserVM.getWorkArea());
        
        userRepository.save(user);
        userSearchRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            userSearchRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public void updateUser(Long id, String login, String firstName, String lastName, String email,
        boolean activated, String langKey, Set<String> authorities, ManagedUserVM managedUserVM) {

        Optional.of(userRepository
            .findOne(id))
            .ifPresent(user -> {
                user.setLogin(login);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setActivated(activated);
                user.setLangKey(langKey);
                
                if(!StringUtil.isNullStr(managedUserVM.getPassword())){
                	String password = managedUserVM.getPassword();
                	String encryptedPassword = passwordEncoder.encode(password);
                	user.setPassword(encryptedPassword);
                }
                
                user.setSerialNum(managedUserVM.getSerialNum());
                user.setDeptId(managedUserVM.getDeptId());
                user.setIsManager(managedUserVM.getIsManager());
                user.setDuty(managedUserVM.getDuty());
                user.setGrade(managedUserVM.getGrade());
                user.setGender(managedUserVM.getGender());
                user.setBirthYear(managedUserVM.getBirthYear());
                user.setBirthDay(managedUserVM.getBirthDay());
                user.setTelephone(managedUserVM.getTelephone());
                user.setWorkArea(managedUserVM.getWorkArea());
                
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                authorities.forEach(
                    authority -> managedAuthorities.add(authorityRepository.findOne(authority))
                );
                log.debug("Changed Information for User: {}", user);
            });
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            userSearchRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
    	List<Object[]> objs = userRepository.findDetailByLogin(login);
    	User returnUser = null;
    	if(objs != null && !objs.isEmpty()){
    		returnUser = (User) objs.get(0)[0];
    		returnUser.setDept(StringUtil.null2Str(objs.get(0)[1]));
    	}
    	Optional<User> us = Optional.of(returnUser);
        return us.map(user -> {
            user.getAuthorities().size();
            return user;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        User user = userRepository.findOne(id);
        user.getAuthorities().size(); // eagerly load the association
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        User user = null;
        if (optionalUser.isPresent()) {
          user = optionalUser.get();
            user.getAuthorities().size(); // eagerly load the association
         }
         return user;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
//            userRepository.delete(user);
//            userSearchRepository.delete(user);
        }
    }

	public Page<User> getUserPage(User user, Pageable pageable) {
		return userDao.getUserPage(user, pageable);
	}

	public List<Authority> queryAllAuthorities() {
		List<Authority> list = authorityRepository.findAll();
		return list;
	}

	public Map<String, UserBaseVo> getAllUser() {
		List<User> list = userRepository.findAll();
		Map<String, UserBaseVo> returnMap = new HashMap<String, UserBaseVo>();
		if(list != null){
			for(User user : list){
				returnMap.put(user.getSerialNum(), new UserBaseVo(user));
			}
		}
		return returnMap;
	}
}
