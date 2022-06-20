package de.janeckert.ga2fa.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.janeckert.ga2fa.entities.Identity;

@Repository
public interface IdentityRepository extends CrudRepository<Identity, Long> {
	public Iterable<Identity> findAll();
	public Optional<Identity> findByName(String name);
}
