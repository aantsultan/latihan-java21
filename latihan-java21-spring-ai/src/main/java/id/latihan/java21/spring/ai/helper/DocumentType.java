package id.latihan.java21.spring.ai.helper;

public enum DocumentType {

    PDF;

    public enum ImageType {
        JPG,
        JPEG,
        PNG;

        public String extension() {
            return "."+ name().toLowerCase();
        }
    }

    public String extension() {
        return "."+ name().toLowerCase();
    }
}
