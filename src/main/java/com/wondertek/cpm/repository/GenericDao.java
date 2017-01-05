package com.wondertek.cpm.repository; 

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean; 

@NoRepositoryBean 
public interface GenericDao<T, PK extends Serializable> 
	extends JpaRepository<T, PK>, JpaSpecificationExecutor<T>, InitializingBean { 

   Class<T> getDomainClass(); 

   EntityManager getEntityManager(); 
   /**
    * 创建分页对象
    * @return
    */
   Pageable buildPageable(int start, int limit, Order... orders);
   /**
    * 分页的SQL
    * @return
    */
   Page querySqlPage(String querySql, String countSql, Object[] params, Pageable pageable);
   Page querySqlPage(String querySql, String countSql, Object[] params, Map<String,Object> paramNameList, Pageable pageable);
   /**
    * 分页的HQL
    * @return
    */
   Page queryHqlPage(String queryHql, String countHql, Object[] params, Pageable pageable);
   Page queryHqlPage(String queryHql, String countHql, Object[] params, Map<String,Object> paramNameList, Pageable pageable);
   /**
    * 查询SQL
    * @return
    */
   List queryAllSql(String querySql, Object[] params);
   List queryAllSql(String querySql, Object[] params, Map<String,Object> paramNameList);
   /**
    * 查询SQL
    * @return
    */
   List querySql(String querySql, Object[] params, Pageable pageable);
   List querySql(String querySql, Object[] params, Map<String,Object> paramNameList, Pageable pageable);
   /**
    * 查询HQL
    * @return
    */
   List queryAllHql(String queryHql, Object[] params);
   List queryAllHql(String queryHql, Object[] params, Map<String,Object> paramNameList);
   /**
    * 查询HQL
    * @return
    */
   List queryHql(String queryHql, Object[] params, Pageable pageable);
   List queryHql(String queryHql, Object[] params, Map<String,Object> paramNameList, Pageable pageable);
   /**
    * 查询总数的SQL
    * @return
    */
   long countSql(String countSql, Object[] params);
   long countSql(String countSql, Object[] params, Map<String,Object> paramNameList);
   /**
    * 查询总数的HQL
    * @return
    */
   long countHql(String countHql, Object[] params);
   long countHql(String countHql, Object[] params, Map<String,Object> paramNameList);
   
   /**
    * 执行某个SQL
    * @return
    */
   int excuteSql(String excuteSql, Object[] params);
   int excuteSql(String excuteSql, Object[] params, Map<String,Object> paramNameList);
   /**
    * 执行某个HQL
    * @return
    */
   int excuteHql(String excuteHql, Object[] params);
   int excuteHql(String excuteHql, Object[] params, Map<String,Object> paramNameList);
   
}