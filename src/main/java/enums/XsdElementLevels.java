package enums;

public enum XsdElementLevels {
    ELEMENT_1("xsd:simpleType"), ELEMENT_2("xsd:restriction"), ELEMENT_3("xsd:enumeration");

    private String element;

    private XsdElementLevels(String element) throws IllegalArgumentException {
        if (element == null)
            throw new IllegalArgumentException("Element name is not accepted.");
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }
}
