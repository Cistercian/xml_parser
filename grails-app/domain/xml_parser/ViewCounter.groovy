package xml_parser

/**
 * Счетчик просмотров товаров.
 */
class ViewCounter {

    static constraints = {
    }

    Short count                             //rол-во просмотров
    Long timestamp                          //временной штамп (в минутах)

    static belongsTo = [product : Product]  //ссылка на просматриваемый товар
}
