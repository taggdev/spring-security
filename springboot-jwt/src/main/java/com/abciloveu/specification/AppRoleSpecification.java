package com.abciloveu.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.abciloveu.model.AppRoleCriteria;
import com.abciloveu.entities.AppRole;

public class AppRoleSpecification {

	public static Specification<AppRole> toPredicate(AppRoleCriteria criteria) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new ArrayList<>();
			if (criteria.getId() != null) {
				predicates.add(cb.equal(root.get("functionId"), criteria.getId()));
			}

			if (!StringUtils.isEmpty(criteria.getRoleName())) {
				predicates.add(cb.like(cb.lower(root.get("roleName")),
						"%" + criteria.getRoleName().toLowerCase() + "%"));
			}

			if (!StringUtils.isEmpty(criteria.getDescription())) {
				predicates.add(cb.like(cb.lower(root.get("description")),
						"%" + criteria.getDescription().toLowerCase() + "%"));
			}
			
			if (!StringUtils.isEmpty(criteria.getPrivileges())) {
				predicates.add(cb.like(cb.lower(root.get("privileges")),
						"%" + criteria.getPrivileges().toLowerCase() + "%"));
			}
			

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
}
