package xml_parser

class Category {

    static constraints = {
    }

    Byte grade;                             //числовая оценка 0..2
    String name;                            //Наименование категории "плохая"/"хорошая"/"отличная"

    static hasMany = [products: Product]    //ссылка на товар

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", grade=" + grade +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
