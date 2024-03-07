package com.abciloveu.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import com.abciloveu.entites.AppUser;
import com.abciloveu.model.AppUserCriteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class AppUserSpecification {

	public static Specification<AppUser> toPredicate(AppUserCriteria criteria) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new ArrayList<>();
			if (criteria.getId() != null) {
				predicates.add(cb.equal(root.get("id"), criteria.getId()));
			}

			if (!StringUtils.isEmpty(criteria.getUsername())) {
				predicates.add(cb.like(cb.lower(root.get("username")),
						"%" + criteria.getUsername().toLowerCase() + "%"));
			}
			
			if (!StringUtils.isEmpty(criteria.getDisplayName())) {
				predicates.add(cb.like(cb.lower(root.get("displayName")),
						"%" + criteria.getDisplayName().toLowerCase() + "%"));
			}
			
			if (!StringUtils.isEmpty(criteria.getContactName())) {
				predicates.add(cb.like(cb.lower(root.get("contactName")),
						"%" + criteria.getContactName().toLowerCase() + "%"));
			}
			
			if (criteria.getEnabled() != null) {
				predicates.add(cb.equal(cb.lower(root.get("enable")), criteria.getEnabled()));
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

}
