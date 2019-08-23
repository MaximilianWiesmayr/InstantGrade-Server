package entity;


/**
 * @author Sebastian Schiefermayr
 */
public class Image {
    private String customName; // Custom Name defined by the User
    private String factoryName; // Original Filename named by the Camera
    private String owner; // The User, who took the picture (username)
    private String extension; // Image Extension (JPG, PNG, RAW)
    private String filepath; // Absolute Path of the image (Future Folder-Structure: uploads/{username}/{image}.{extension} )
    // -= IMAGE SPECIFIC METADATA =-
    private String metadata; // Exif Metadata from library: https://drewnoakes.com/code/exif/

    public Image() {
    }
    public Image(String factoryName, String owner) {
        this.factoryName = factoryName;
        this.owner = owner;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
