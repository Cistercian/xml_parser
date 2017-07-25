package xml_parser

/**
 * Сущность БД, представляющая собой категорию - оценочный параметр основной сущности Product
 */
class Category {

    static constraints = {
    }

    Byte grade;                             //числовая оценка 0..2
    String name;                            //Наименование категории "плохая"/"хорошая"/"отличная"

    static hasMany = [products: Product]    //ссылка на товар (Product)


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Category category = (Category) o

        if (grade != category.grade) return false
        if (name != category.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (grade != null ? grade.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }

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
