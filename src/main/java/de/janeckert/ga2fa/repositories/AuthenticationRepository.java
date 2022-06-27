package de.janeckert.ga2fa.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.entities.Identity;

@Repository
public interface AuthenticationRepository extends CrudRepository<Authentication, Long> {
	public Iterable<Authentication> findByDevice(String device);
}
