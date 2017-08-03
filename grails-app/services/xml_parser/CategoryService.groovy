package xml_parser

import grails.gorm.transactions.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Сервисный уровень для сущностей Category.
 * Предзаполнение БД статичными записями (не предполагается ручное изменение таблицы) и поиск этих данных
 */
@Transactional
class CategoryService {

    //Статический список для исключения многократного обращения к БД
    static def categories

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    /**
     * Функция создания записей Category (заполнение categories). Запускается при запуске приложения.
     */
    void initMockData() {
        log.debug("initMockData()")

        def currentCategories = Category.list();
        def mockData = new ArrayList<Category>();

        mockData.add(new Category(grade: (byte) 0, name: "Плохой"));
        mockData.add(new Category(grade: (byte) 1, name: "Хороший"));
        mockData.add(new Category(grade: (byte) 2, name: "Отличный"));

        for (category in mockData) {
            if (!currentCategories.contains(category)) {
                log.info("category ${category} не найдена! Создаем...")
                category.save(flush: true)
            } else
                log.info("category ${category} найдена.")
        }

        categories = Category.listOrderByGrade();
    }

    /**
     * Функция возвращает категорию, соответствующую переданному рейтингу
     * @param rating текущий рейтинг
     * @return Category (при значении рейтинга, не укладывающего в оговоренные рамки, возвращается минимальная категория)
     */
    Category getByRating(Float rating) {
        logger.debug("getByRating()")

        if (Float.compare(rating, 3f) > 0 && Float.compare(rating, 4f) <= 0) {
            log.debug("${rating} - относим к категории Хорошая")
            return categories.get(1)
        } else if (Float.compare(rating, 4f) > 0 && Float.compare(rating, 5f) <= 0) {
            log.debug("${rating} - относим к категории Отличная")
            return categories.get(2)
        }

        log.debug("${rating} - относим к категории Плохая")
        return categories.get(0)
        
    }
}
