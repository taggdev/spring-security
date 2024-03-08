package com.abciloveu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.abciloveu.exception.BadResourceException;
import com.abciloveu.exception.RecordAlreadyExistsException;
import com.abciloveu.exception.RecordNotFoundException;
import com.abciloveu.model.AppRoleCriteria;
import com.abciloveu.model.SearchCriteria;
import com.abciloveu.repository.AppRoleRepository;
import com.abciloveu.specification.AppRoleSpecification;
import com.abciloveu.entities.AppRole;

@Service
@Transactional(readOnly = true, rollbackFor= {Exception.class})
public class AppRoleService {

	private final AppRoleRepository repo;

	@Autowired
	public AppRoleService(AppRoleRepository AppRoleRepository) {
		this.repo = AppRoleRepository;
	}

	private PageRequest buildPageRequest(SearchCriteria criteria) {
		
		if(StringUtils.isBlank(criteria.getSort())){
			return PageRequest.of(
					criteria.getPageNo() - 1, 
					criteria.getPageSize());
		}
		
		else {
			return PageRequest.of(
				criteria.getPageNo() - 1, 
				criteria.getPageSize(), 
				Optional.of(criteria.getDirection()).orElse(Sort.Direction.ASC),
				criteria.getSort());
		}
	}

	private boolean existsById(Long id) {
		return repo.existsById(id);
	}

	public Page<AppRole> findAll(AppRoleCriteria criteria) {
		return repo.findAll(AppRoleSpecification.toPredicate(criteria), buildPageRequest(criteria));
	}
	
	@Cacheable(cacheNames = "roles")
	public List<AppRole> findAllRoles() {
		return this.repo.findAll();
	}
	
	
	public AppRole findById(Long id) throws RecordNotFoundException {
		final AppRole contact = repo.findById(id)
				.orElseThrow(	() -> new RecordNotFoundException("Cannot find appRole with id: " + id));

		return contact;
	}

	@Transactional(readOnly = false, rollbackFor= Exception.class)
	public AppRole save(AppRole appRole) throws BadResourceException, RecordAlreadyExistsException {
		if (appRole.getId() != null) {
			if (existsById(appRole.getId())) {
				throw new RecordAlreadyExistsException(
						"appRole with id: " + appRole.getId() + " already exists");
			}
			return repo.save(appRole);
		}

		else {
			final BadResourceException exc = new BadResourceException("Failed to save AppRole");
			exc.addErrorMessage("AppRole ID is null or empty");
			throw exc;
		}
	}

	@Transactional(readOnly = false, rollbackFor= Exception.class)
	public Optional<AppRole> update(Long id, AppRole entity) {
		Optional<AppRole> optionalappRole = repo.findById(id);
		if (!optionalappRole.isPresent()) {
			return optionalappRole;
		}

		entity.setId(id);

		return Optional.of(repo.save(entity));
	}

	@Transactional(readOnly = false, rollbackFor= Exception.class)
	public void update(AppRole appRole) throws BadResourceException, RecordNotFoundException {
		if (appRole.getId() != null) {
			if (!existsById(appRole.getId())) {
				throw new RecordNotFoundException("Cannot find appRole with id: " + appRole.getId());
			}

			repo.save(appRole);
		}
		else {
			final BadResourceException exc = new BadResourceException("Failed to save appRole");
			exc.addErrorMessage("appRole is null or empty");
			throw exc;
		}
	}

	@Transactional(readOnly = false, rollbackFor= Exception.class)
	public void deleteById(Long id) throws RecordNotFoundException {
		try {
			repo.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new RecordNotFoundException("Cannot find contact with id: " + id);
		}
	}
}
