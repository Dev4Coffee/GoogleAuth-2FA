package de.janeckert.ga2fa.geo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoCoordinate {
	private Double latitude;
	private Double longitude;
}
