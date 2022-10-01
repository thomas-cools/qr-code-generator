package com.cools.qr.converter;

import ezvcard.property.Organization;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine;

public class OrganizationConverter implements CommandLine.ITypeConverter<Organization> {

    @Override
    public Organization convert(String organizationString) {
        if (organizationString.isBlank()) {
            throw new CommandLine.TypeConversionException("Missing organization");
        }

        String[] split = organizationString.split(":");
        if (ArrayUtils.isEmpty(split) || split.length == 1) {
            Organization organization = new Organization();
            organization.getValues().add(organizationString);
            return organization;
        }

        if (split.length > 2) {
            throw new CommandLine.TypeConversionException(String.format("Organization string is invalid: [%s]. " +
                                                                        "Usage is companyName:team",
                                                                        organizationString));
        }

        Organization organization = new Organization();
        organization.getValues().add(split[0]);
        organization.getValues().add(split[1]);

        return organization;
    }
}
