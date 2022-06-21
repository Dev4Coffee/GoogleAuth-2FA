package de.janeckert.ga2fa.entities;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "Authentications")
@Data
@Builder
public class Authentication {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long authentication_id;
	
	@ManyToOne
	@JoinColumn(name = "identity_id")
	private Identity principal;
	
	private Instant timestamp;
	private Double longitude;
	private Double latitude;
	
}
