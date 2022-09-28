package com.cools.qr.generator;

import com.cools.qr.converter.AddressConverter;
import com.cools.qr.converter.OrganizationConverter;
import com.cools.qr.converter.PhoneTypeConverter;
import com.cools.qr.util.ImageUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Organization;
import ezvcard.property.StructuredName;
import io.nayuki.qrcodegen.QrCode;
import picocli.CommandLine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(name = "QrGenerator", version = "QrGenerator 1.0", mixinStandardHelpOptions = true)
public class QrGenerator implements Runnable {

    private static final Logger log = Logger.getLogger(QrGenerator.class.getName());

    @CommandLine.Parameters(arity = "1", paramLabel = "first name", description = "The contact's first name")
    private String firstName;

    @CommandLine.Parameters(arity = "1", paramLabel = "last name", description = "The contact's last name")
    private String lastName;

    @CommandLine.Parameters(arity = "1", paramLabel = "email", description = "The contact's email address")
    private String email;

    @CommandLine.Parameters(arity = "1", paramLabel = "phoneNumbers", description = "The contact's phone numbers")
    private Map<TelephoneType, String> phoneNumbers;

    @CommandLine.Parameters(paramLabel = "website url", description = "The contact's website url")
    private String websiteUrl;

    @CommandLine.Parameters(arity = "1", paramLabel = "address", description = "The contact's address", converter
            = AddressConverter.class)
    private Address address;

    @CommandLine.Parameters(arity = "1", paramLabel = "organization", description = "The organizations a contact " +
                                                                                    "belongs to, from most to least " +
                                                                                    "specific", converter =
            OrganizationConverter.class)
    private Organization organization;

    public static void main(String... args) {
        QrGenerator qrGenerator = new QrGenerator();
        CommandLine commandLine = new CommandLine(qrGenerator).registerConverter(TelephoneType.class,
                                                                                 new PhoneTypeConverter());

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;

        String vCard = createVCard(firstName, lastName, phoneNumbers, email, websiteUrl, address, organization);

        QrCode qr = QrCode.encodeText(vCard, errCorLvl);

        BufferedImage img     = ImageUtils.toImage(qr, 10, 4); // Convert to bitmap image
        File          imgFile = new File("hello-world-QR.png");   // File path for output
        try {
            ImageIO.write(img, "png", imgFile);                     // Write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write png image file", e);
            throw new RuntimeException(e);
        }

        String svg     = ImageUtils.toSvgString(qr, 1, "#FFFFFF", "#000000");  // Convert to SVG XML code
        File   svgFile = new File("carolina-website.svg");          // File path for output
        try {
            Files.writeString(svgFile.toPath(), svg); // write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write svg image file", e);
            throw new RuntimeException(e);
        }
    }

    private String createVCard(String firstName, String lastName, Map<TelephoneType, String> phoneNumbers, String email,
                               String websiteUrl, Address address, Organization organization) {
        VCard vcard = new VCard();

        StructuredName n = new StructuredName();
        n.setFamily(lastName);
        n.setGiven(firstName);
        vcard.setStructuredName(n);

        vcard.setFormattedName(String.join(" ", firstName, lastName));
        vcard.setOrganization(organization);

        phoneNumbers.forEach((key, value) -> vcard.addTelephoneNumber(value, key));

        vcard.addTelephoneNumber("+310648032045", TelephoneType.CELL);
        vcard.addEmail(email);
        vcard.addUrl(websiteUrl);
        vcard.addAddress(address);

        return Ezvcard.write(vcard).version(VCardVersion.V4_0).go();
    }
}
