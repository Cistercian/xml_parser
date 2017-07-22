package xml_parser

class ViewCounter {

    static constraints = {
    }

    Short count                             //вол-во просмотров
    Long timestamp                          //временной штамп (минута)

    static belongsTo = [product : Product]  //ссылка на просматриваемый товар
}
