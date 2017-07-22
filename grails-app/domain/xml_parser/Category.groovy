package xml_parser

class Category {

    static constraints = {
    }

    Byte grade;
    String name;

    static hasMany = [products: Product]

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", grade=" + grade +
                ", name='" + name + '\'' +
                ", version=" + version +
                //", products=" + products +
                '}';
    }
}
