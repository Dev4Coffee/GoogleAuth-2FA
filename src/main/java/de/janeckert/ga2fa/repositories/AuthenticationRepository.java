package de.janeckert.ga2fa.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.janeckert.ga2fa.entities.Authentication;

@Repository
public interface AuthenticationRepository extends CrudRepository<Authentication, Long> {
}
