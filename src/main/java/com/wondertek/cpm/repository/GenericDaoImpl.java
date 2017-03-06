package com.wondertek.cpm.repository; 

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.wondertek.cpm.config.Constants;
import com.wondertek.cpm.config.StringUtil; 

@NoRepositoryBean
@Transactional
public abstract class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T,PK> { 
	
	private EntityManager entityManager;
	
	protected SimpleJpaRepository<T,PK> simpleJpaRepository;
	
	@Override
	public List<T> findAll() {
		return simpleJpaRepository.findAll();
	}
	
	@Override
	public List<T> findAll(Sort sort) {
		return simpleJpaRepository.findAll(sort);
	}
	
	@Override
	public List<T> findAll(Iterable<PK> ids) {
		return simpleJpaRepository.findAll(ids);
	}
	
	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		return simpleJpaRepository.save(entities);
	}
	
	@Override
	public void flush() {
		simpleJpaRepository.flush();
	}
	
	@Override
	public <S extends T> S saveAndFlush(S entity) {
		return simpleJpaRepository.saveAndFlush(entity);
	}
	
	@Override
	public void deleteInBatch(Iterable<T> entities) {
		simpleJpaRepository.deleteInBatch(entities);
	}
	
	@Override
	public void deleteAllInBatch() {
		simpleJpaRepository.deleteAllInBatch();
	}
	
	@Override
	public T getOne(PK id) {
		return simpleJpaRepository.getOne(id);
	}
	
	@Override
	public <S extends T> List<S> findAll(Example<S> example) {
		return simpleJpaRepository.findAll(example);
	}
	
	@Override
	public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
		return simpleJpaRepository.findAll(example, sort);
	}
	
	@Override
	public Page<T> findAll(Pageable pageable) {
		return simpleJpaRepository.findAll(pageable);
	}
	
	@Override
	public <S extends T> S save(S entity) {
		return simpleJpaRepository.save(entity);
	}
	
	@Override
	public T findOne(PK id) {
		return simpleJpaRepository.findOne(id);
	}
	
	@Override
	public boolean exists(PK id) {
		return simpleJpaRepository.exists(id);
	}
	
	@Override
	public long count() {
		return simpleJpaRepository.count();
	}
	
	@Override
	public void delete(PK id) {
		simpleJpaRepository.delete(id);
	}
	
	@Override
	public void delete(T entity) {
		simpleJpaRepository.delete(entity);
	}
	
	@Override
	public void delete(Iterable<? extends T> entities) {
		simpleJpaRepository.delete(entities);
	}
	
	@Override
	public void deleteAll() {
		simpleJpaRepository.deleteAll();
	}
	
	@Override
	public <S extends T> S findOne(Example<S> example) {
		return simpleJpaRepository.findOne(example);
	}
	
	@Override
	public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
		return simpleJpaRepository.findAll(example, pageable);
	}
	
	@Override
	public <S extends T> long count(Example<S> example) {
		return simpleJpaRepository.count(example);
	}
	
	@Override
	public <S extends T> boolean exists(Example<S> example) {
		return simpleJpaRepository.exists(example);
	}
	
	@Override
	public T findOne(Specification<T> spec) {
		return simpleJpaRepository.findOne(spec);
	}
	
	@Override
	public List<T> findAll(Specification<T> spec) {
		return simpleJpaRepository.findAll(spec);
	}
	
	@Override
	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
		return simpleJpaRepository.findAll(spec, pageable);
	}
	
	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		return simpleJpaRepository.findAll(spec, sort);
	}
	
	@Override
	public long count(Specification<T> spec) {
		return simpleJpaRepository.count(spec);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (getDomainClass() != null) {
			simpleJpaRepository = new SimpleJpaRepository<T, PK>(getDomainClass(),getEntityManager());
		}
		if(getEntityManager() != null){
			entityManager = getEntityManager();
		}
	}

	@Override
	public Pageable buildPageable(int start, int limit, Order... orders) {
		Sort sort = null;
		if(orders != null && orders.length > 0){
			sort = new Sort(orders);
		}
		if (start < 0) {
			start = 0;
		}
		if (limit < 1) {
			limit = 20;
		}
		return new PageRequest((int) (start / limit), limit, sort);
	}
	
	@Override
	public Page querySqlPage(String querySql, String countSql, Object[] params, Pageable pageable) {
		return querySqlPage(querySql,countSql,params,null,pageable);
	}
	@Override
	public Page querySqlPage(String querySql, String countSql, Object[] params, Map<String,Object> paramNameList, Pageable pageable) {
		long total = countSql(countSql, params,paramNameList);
		List content = querySql(querySql, params,paramNameList,pageable);
		return new PageImpl(content, pageable, total);
	}
	
	@Override
	public Page queryHqlPage(String queryHql, String countHql, Object[] params, Pageable pageable) {
		return queryHqlPage(queryHql, countHql, params, null, pageable);
	}
	@Override
	public Page queryHqlPage(String queryHql, String countHql, Object[] params, Map<String,Object> paramNameList, Pageable pageable) {
		long total = countHql(countHql, params, paramNameList);
		List content = queryHql(queryHql, params, paramNameList, pageable);
		return new PageImpl(content, pageable, total);
	}
	
	@Override
	public List queryAllSql(String querySql, Object[] params) {
		return queryAllSql(querySql,params,null);
	}
	public List queryAllSql(String querySql, Object[] params, Map<String,Object> paramNameList) {
		return querySql(querySql,params,paramNameList,null);
	}
	@Override
	public List querySql(String querySql, Object[] params, Pageable pageable) {
		return querySql(querySql,params,null,pageable);
	}
	@Override
	public List querySql(String querySql, Object[] params, Map<String,Object> paramNameList, Pageable pageable) {
		Query query = entityManager.createNativeQuery(querySql);
		if (params != null) {
			for (int i = 1; i <= params.length; i++) {
				query.setParameter(i, params[i-1]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		if(pageable != null){
			query.setFirstResult(pageable.getOffset() > 0 ? pageable.getOffset() : 0);
			query.setMaxResults((pageable.getPageSize() > 0 && pageable.getPageSize() < Constants.QUERY_PAGE_MAX_SIZE) ? 
					pageable.getPageSize() : Constants.QUERY_PAGE_MAX_SIZE);
		}
		return query.getResultList();
	}
	@Override
	public List queryAllHql(String queryHql, Object[] params) {
		return queryAllHql(queryHql,params,null);
	}
	@Override
	public List queryAllHql(String queryHql, Object[] params, Map<String,Object> paramNameList) {
		return queryHql(queryHql,params,paramNameList,null);
	}
	@Override
	public List queryHql(String queryHql, Object[] params, Pageable pageable) {
		return queryHql(queryHql,params,null,pageable);
	}
	@Override
	public List queryHql(String queryHql, Object[] params, Map<String,Object> paramNameList, Pageable pageable) {
		Query query = entityManager.createQuery(queryHql);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(""+i, params[i]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		if(pageable != null){
			query.setFirstResult(pageable.getOffset() > 0 ? pageable.getOffset() : 0);
			query.setMaxResults((pageable.getPageSize() > 0 && pageable.getPageSize() < Constants.QUERY_PAGE_MAX_SIZE) ? 
					pageable.getPageSize() : Constants.QUERY_PAGE_MAX_SIZE);
		}
		return query.getResultList();
	}
	@Override
	public long countSql(String countSql, Object[] params) {
		return countSql(countSql,params,null);
	}
	@Override
	public long countSql(String countSql, Object[] params, Map<String,Object> paramNameList) {
		Query query = entityManager.createNativeQuery(countSql);
		if (params != null) {
			for (int i = 1; i <= params.length; i++) {
				query.setParameter(i, params[i-1]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		return StringUtil.nullToLong(query.getSingleResult());
	}
	@Override
	public long countHql(String countHql, Object[] params) {
		return countHql(countHql,params,null);
	}
	@Override
	public long countHql(String countHql, Object[] params, Map<String,Object> paramNameList) {
		TypedQuery<Long> query = entityManager.createQuery(countHql,Long.class);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(""+i, params[i]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		return query.getSingleResult();
	}

	@Override
	public int excuteSql(String excuteSql, Object[] params) {
		return excuteSql(excuteSql,params,null);
	}

	@Override
	public int excuteSql(String excuteSql, Object[] params, Map<String, Object> paramNameList) {
		Query query = entityManager.createNativeQuery(excuteSql);
		if (params != null) {
			for (int i = 1; i <= params.length; i++) {
				query.setParameter(i, params[i-1]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		return query.executeUpdate();
	}

	@Override
	public int excuteHql(String excuteHql, Object[] params) {
		return excuteHql(excuteHql,params,null);
	}
	@Override
	public int excuteHql(String excuteHql, Object[] params, Map<String, Object> paramNameList) {
		Query query = entityManager.createQuery(excuteHql);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(""+i, params[i]);
			}
		}
		if (paramNameList != null) {
			for (Iterator<String> it = paramNameList.keySet().iterator(); it.hasNext();) {
				String columnName = it.next();
				query.setParameter(columnName, paramNameList.get(columnName));
			}
		}
		return query.executeUpdate();
	}
}