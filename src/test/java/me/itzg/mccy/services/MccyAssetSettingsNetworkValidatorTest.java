package me.itzg.mccy.services;

import me.itzg.mccy.config.MccyAssetSettings;
import org.hamcrest.Matchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class MccyAssetSettingsNetworkValidatorTest {

    private LocalValidatorFactoryBean validatorFactoryBean;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

    }

    @Test
    public void testViolatesNone() throws Exception {
        final Validator validator = validatorFactoryBean.getValidator();

        final MccyAssetSettings settings = new MccyAssetSettings();
        settings.setStorageDir(temporaryFolder.newFolder());
        settings.setVia(MccyAssetSettings.Via.NETWORK);
        settings.setMyNameOnNetwork("mccy-engine");
        settings.setNetwork("mccy-overlay");

        final Set<ConstraintViolation<MccyAssetSettings>> violations = validator.validate(settings);
        assertThat(violations, Matchers.empty());
    }

    @Test
    public void testNotEvenApplicable() throws Exception {

        final Validator validator = validatorFactoryBean.getValidator();

        final MccyAssetSettings settings = new MccyAssetSettings();
        settings.setStorageDir(temporaryFolder.newFolder());
        final Set<ConstraintViolation<MccyAssetSettings>> violations = validator.validate(settings);
        assertThat(violations, Matchers.empty());

    }

    @Test
    public void testViolatesPartial() throws Exception {

        final Validator validator = validatorFactoryBean.getValidator();

        final MccyAssetSettings settings = new MccyAssetSettings();
        settings.setStorageDir(temporaryFolder.newFolder());
        settings.setVia(MccyAssetSettings.Via.NETWORK);
        settings.setNetwork("mccy-overlay");

        final Set<ConstraintViolation<MccyAssetSettings>> violations = validator.validate(settings);
        assertThat(violations, Matchers.hasSize(1));

    }

    @Test
    public void testViolatesAll() throws Exception {
        final Validator validator = validatorFactoryBean.getValidator();

        final MccyAssetSettings settings = new MccyAssetSettings();
        settings.setVia(MccyAssetSettings.Via.NETWORK);

        final Set<ConstraintViolation<MccyAssetSettings>> violations = validator.validate(settings);

        assertThat(violations, Matchers.hasSize(3));

    }
}