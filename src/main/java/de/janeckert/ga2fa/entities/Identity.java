package de.janeckert.ga2fa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "Identities")
@Data
public class Identity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long identity_id;
	private String name;
	private String password;
	
	private String mfaStrategy;
	private String token;
	
	private Boolean active;
	
	private String googleAuthSecret;
}
