package com.abciloveu.web.controller;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abciloveu.entities.AppRole;
import com.abciloveu.model.AppRoleCriteria;
import com.abciloveu.service.AppRoleService;

@Validated
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = AppRoleController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppRoleController {

	private static final Logger LOG = LoggerFactory.getLogger(AppRoleController.class);

	static final String PATH = "/api/v1/approle";
	
	private final AppRoleService service;

	public AppRoleController(final AppRoleService approleService) {
		this.service = approleService;
	}

	@GetMapping	
	public ResponseEntity<Page<AppRole>> findAll(AppRoleCriteria criteria) {
		return ResponseEntity.ok(service.findAll(criteria));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AppRole> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}
	
	
	@PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EDITOR')")
	@PostMapping
	public ResponseEntity<AppRole> create(@Valid @RequestBody AppRole approle) {
		AppRole savedapprole = service.save(approle);
		
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedapprole)
				.toUri();
		
		return ResponseEntity.created(location).body(savedapprole);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EDITOR')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody AppRole approle) {
		approle.setId(id);
		service.update(approle);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EDITOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		service.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
