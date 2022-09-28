package com.cools.qr;

import com.cools.qr.converter.AddressConverter;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(name = "QrGenerator", version = "QrGenerator 1.0", mixinStandardHelpOptions = true)
public class QrGenerator implements Runnable {

    private static final Logger log = Logger.getLogger(QrGenerator.class.getName());

    @CommandLine.Parameters(arity = "1..*", paramLabel = "first name", description = "The contact's first name")
    private String firstName;

    @CommandLine.Parameters(arity = "1..*", paramLabel = "last name", description = "The contact's last name")
    private String lastName;

    @CommandLine.Parameters(arity = "1..*", paramLabel = "email", description = "The contact's email address")
    private String email;

    @CommandLine.Parameters(arity = "1..*", paramLabel = "phoneNumbers", description = "The contact's phone numbers")
    private Map<TelephoneType, String> phoneNumbers;

    @CommandLine.Parameters(paramLabel = "website url", description = "The contact's website url")
    private String websiteUrl;

    @CommandLine.Parameters(arity = "1..*", paramLabel = "address", description = "The contact's address", converter
            = AddressConverter.class)
    private Address address;

    public static void main(String... args) {
        int exitCode = new CommandLine(new QrGenerator()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;

        String vCard = createVCard(firstName, lastName,phoneNumbers, email, websiteUrl, address);

        QrCode qr = QrCode.encodeText(vCard, errCorLvl);

        BufferedImage img     = toImage(qr, 10, 4);           // Convert to bitmap image
        File          imgFile = new File("hello-world-QR.png");   // File path for output
        try {
            ImageIO.write(img, "png", imgFile);                     // Write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write png image file", e);
            throw new RuntimeException(e);
        }

        String svg     = toSvgString(qr, 1, "#FFFFFF", "#000000");  // Convert to SVG XML code
        File   svgFile = new File("carolina-website.svg");          // File path for output
        try {
            Files.writeString(svgFile.toPath(), svg); // write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write svg image file", e);
            throw new RuntimeException(e);
        }
    }

    private BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    /**
     * Returns a raster image depicting the specified QR Code, with
     * the specified module scale, border modules, and module colors.
     * <p>For example, scale=10 and border=4 means to pad the QR Code with 4 light border
     * modules on all four sides, and use 10&#xD7;10 pixels to represent each module.
     *
     * @param qr         the QR Code to render (not {@code null})
     * @param scale      the side length (measured in pixels, must be positive) of each module
     * @param border     the number of border modules to add, which must be non-negative
     * @param lightColor the color to use for light modules, in 0xRRGGBB format
     * @param darkColor  the color to use for dark modules, in 0xRRGGBB format
     * @return a new image representing the QR Code, with padding and scaling
     * @throws NullPointerException     if the QR Code is {@code null}
     * @throws IllegalArgumentException if the scale or border is out of range, or if
     *                                  {scale, border, size} cause the image dimensions to exceed Integer.MAX_VALUE
     */
    private BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
            throw new IllegalArgumentException("Scale or border too large");
        }

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale,
                                                 BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    /**
     * Returns a string of SVG code for an image depicting the specified QR Code, with the specified
     * number of border modules. The string always uses Unix newlines (\n), regardless of the platform.
     *
     * @param qr         the QR Code to render (not {@code null})
     * @param border     the number of border modules to add, which must be non-negative
     * @param lightColor the color to use for light modules, in any format supported by CSS, not {@code null}
     * @param darkColor  the color to use for dark modules, in any format supported by CSS, not {@code null}
     * @return a string representing the QR Code as an SVG XML document
     * @throws NullPointerException     if any object is {@code null}
     * @throws IllegalArgumentException if the border is negative
     */
    private String toSvgString(QrCode qr, int border, String lightColor, String darkColor) {
        Objects.requireNonNull(qr);
        Objects.requireNonNull(lightColor);
        Objects.requireNonNull(darkColor);
        if (border < 0) {
            throw new IllegalArgumentException("Border must be non-negative");
        }
        StringBuilder sb = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                                              .append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www" +
                                                      ".w3.org/Graphics/SVG/1" + ".1/DTD/svg11.dtd\">\n")
                                              .append(String.format(
                                                      "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" " +
                                                      "viewBox=\"0 0 %1$d %1$d\" " + "stroke=\"none\">\n",
                                                      qr.size + (long) border * 2))
                                              .append("\t<rect width=\"100%\" height=\"100%\" fill=\"")
                                              .append(lightColor).append("\"/>\n").append("\t<path d=\"");
        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    if (x != 0 || y != 0) {
                        sb.append(" ");
                    }
                    sb.append(String.format("M%d,%dh1v1h-1z", x + (long) border, y + (long) border));
                }
            }
        }
        return sb.append("\" fill=\"").append(darkColor).append("\"/>\n").append("</svg>\n").toString();
    }

    private String createVCard(String firstName,
                               String lastName,
                               Map<TelephoneType, String> phoneNumbers,
                               String email,
                               String websiteUrl,
                               Address address) {
        VCard vcard = new VCard();

        StructuredName n = new StructuredName();
        n.setFamily(lastName);
        n.setGiven(firstName);
        vcard.setStructuredName(n);

        vcard.setFormattedName(String.join(" ", firstName, lastName);
        Organization organization = new Organization();
        organization.set
        vcard.setOrganization();

        phoneNumbers.forEach((key, value) -> vcard.addTelephoneNumber(value, key));

        vcard.addTelephoneNumber("+310648032045", TelephoneType.CELL);
        vcard.addEmail(email);
        vcard.addUrl(websiteUrl);
        vcard.addAddress(address);

        return Ezvcard.write(vcard).version(VCardVersion.V4_0).go();
    }
}
