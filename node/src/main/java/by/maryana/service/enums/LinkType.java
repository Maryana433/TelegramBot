package by.maryana.service.enums;

public enum LinkType {
    GET_DOC("file/get-doc"),
    GET_PHOTO("file/get-photo");

    private final String url;

     LinkType(String url) {
        this.url = url;
    }

    public String toString(){
         return url;
    }
}
