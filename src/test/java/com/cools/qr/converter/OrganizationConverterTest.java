package com.cools.qr.converter;


import ezvcard.property.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class OrganizationConverterTest {

    private final OrganizationConverter organizationConverter = new OrganizationConverter();

    @Test
    void testSuccessfulParse() {
        String       orgString    = "aldeco:assessorimobilario";
        Organization organization = Assertions.assertDoesNotThrow(() -> organizationConverter.convert(orgString));
        Assertions.assertNotNull(organization);
        Assertions.assertFalse(organization.getValues().isEmpty());
        Assertions.assertEquals("aldeco", organization.getValues().get(0));
        Assertions.assertEquals("assessorimobilario", organization.getValues().get(1));
    }

    @Test
    void testFailedParseThrowsTypeConversionException() {
        String orgString = "aldeco";
        Assertions.assertThrows(CommandLine.TypeConversionException.class,
                                () -> organizationConverter.convert(orgString));
    }
}