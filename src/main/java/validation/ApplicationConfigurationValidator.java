package validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;

public class ApplicationConfigurationValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationConfiguration.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationConfiguration cfg = (ApplicationConfiguration)target;
		
		
		if (!ApplicationConfiguration.supportedMfaStrategies.contains(cfg.getMfaStrategy())) {
			errors.rejectValue("mfaStrategy", null, "Value does not correspond to any of the supported values: " + ApplicationConfiguration.supportedMfaStrategies);
		}

	}

}
