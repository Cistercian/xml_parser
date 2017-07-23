package xml_parser

/**
 * Главная сущность программы.
 */
class Product {

    static constraints = {
        productId(min: 0, max: Integer.MAX_VALUE)
        title(blank: true, nullable: true, size: 0..255)
        description(blank: true, nullable: true)
        rating(scale: 2)
        price(scale: 2)
        image(blank: true, nullable: true, size: 0..255)
    }
    static mapping = {
        description type: 'text'
        //image sqlType : 'blob'
    }

    Integer productId;                              //ID товара (в сторонней базе)
    String title;                                   //наименование товара
    String description;                             //описание
    Float rating;                                   //рейтинг (оценка). Используется для определения связанной сущности Category
    BigDecimal price;                               //цена
    String image;   //byte[] image;                 //ссылка на картинку

    static hasOne =  [category: Category]           //ссылка на сущность категории
    static hasMany = [viewCounters : ViewCounter]   //ссылки на сущности из таблицы счетчиков просмотра (группировка по минутам)

    /**
     * Функция получения имени категории (для использования в gsp)
     * @return String
     */
    public def getCategoryName(){
        if (this.id == null)
            return ""
        //LazyInitializationException
        if (!this.isAttached()) {
            this.attach()
        }
        return category.name
    }

    /**
     * Функция возвращает оценку категории (gsp)
     * @return Integer
     */
    public def getCategoryGrade() {
        if (this.id == null)
            return 0
        //LazyInitializationException
        if (!this.isAttached()) {
            this.attach()
        }
        return category != null && category.grade != null ? category.grade : 0
    }

    /**
     * Функция получения суммарного кол-ва просмотра данного товара (gsp)
     * @return Integer
     */
    public def getTotalCount(){
        def totalCount = 0

        viewCounters.collect(){ curCounter ->
            totalCount += curCounter.count
        }

        return totalCount
    }

    /**
     * Функция возвращает укороченное поле description (отображение в таблице в gsp)
     * @return String
     */
    public def getFormattedDescription(){
        return description != null && description.length() > 200 ?
                (description.substring(0,200) + "...") :
                description
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productId=" + productId +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", version=" + version +
                '}';
    }
}
