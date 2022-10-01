package com.cools.qr.converter;

import ezvcard.property.Role;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

public class RoleConverter implements CommandLine.ITypeConverter<Role> {

    @Override
    public Role convert(String roleString) throws Exception {
        if (StringUtils.isEmpty(roleString)) {
            throw new CommandLine.TypeConversionException("Role is emptyt or blank");
        }

        return new Role(roleString);
    }
}
