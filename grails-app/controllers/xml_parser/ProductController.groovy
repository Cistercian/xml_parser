package xml_parser

import grails.converters.JSON
import grails.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.SQLException

import static org.springframework.http.HttpStatus.*

/**
 * Контроллер для манипулирования записями таблицы Product (CRUD)
 */
@Transactional(readOnly = false)
class ProductController {

    def viewCounterService
    def productService

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    static allowedMethods = [save: "POST", update: "POST", show: "GET"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Product.list(params), model: [productCount: Product.count()]
    }

    def show(Product product) {
        logger.debug("show()")

        //игнорируем попытки просмотреть несуществуюущю запись
        if (product != null) {
            //исключаем ожидание клиента из-за возможной очереди на запись счетчика
            new Thread(new Runnable() {
                @Override
                void run() {
                    viewCounterService.incrementCounter(product)
                    logger.debug("${Thread.currentThread().getName()}: Счетчик просмотра увеличен.")
                }
            }).start()
        }

        respond product
    }

    def create() {
        respond new Product(params)
    }

    @Transactional
    def save(Product product) {
        if (product == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        //grails & locale ... нивелируем ошибку парсинга символа разделителя целой и дробной части
        productService.fixNumberParsing(product, params)

        if (product.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond product.errors, view: 'create'
            return
        }

        /**
         * Проверяем валидацию поля title и на стороне клиента, и на стороне сервера
         */
        if (!(product.title ==~ '^[а-яА-Я0-9 ]+$')) {
            logger.info("не пройдена валидация! title:${product.title}")

            transactionStatus.setRollbackOnly()
            product.errors.rejectValue('title', 'title.validation.error')
            respond product.errors, view: 'edit'
            return
        }

        product.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'product.label', default: 'Product'), product.id])
                redirect product
            }
            '*' { respond product, [status: CREATED] }
        }
    }

    def edit(Product product) {
        respond product
    }

    @Transactional
    def update(Product product) {
        logger.debug("update()")

        if (product == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        //grails & locale ... нивелируем ошибку парсинга символа разделителя целой и дробной части
        productService.fixNumberParsing(product, params)

        if (product.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond product.errors, view: 'edit'
            return
        }

        /**
         * Проверяем валидацию поля title и на стороне клиента, и на стороне сервера
         */
        if (!(product.title ==~ '^[а-яА-Я0-9 ]+$')) {
            logger.info("не пройдена валидация! title:${product.title}")

            transactionStatus.setRollbackOnly()
            product.errors.rejectValue('title', 'title.validation.error')
            respond product.errors, view: 'edit'
            return
        }

        //не работает автоматическая валидация по полю productId (просто молчаливо игнорируется params.productId > Integer.MAX_VALUE). Почему?
        try{
            product.productId = Integer.valueOf(params.productId)
        } catch (NumberFormatException e) {
            logger.info("Не пройдена валидация! productId:${product.title}. ${e.getMessage()}")

            transactionStatus.setRollbackOnly()
            product.errors.rejectValue('title', 'productId.validation.error')
            respond product.errors, view: 'edit'
            return
        }

        try {
            product.save flush: true
        } catch (SQLException e){
            transactionStatus.setRollbackOnly()
            product.errors.rejectValue('title', e.getMessage())
            respond product.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                //flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'Product'), product.id])
                redirect product
            }
            '*' { respond product, [status: OK] }
        }
    }

    @Transactional
    def delete(Product product) {
        logger.debug("delete")

        if (product == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        product.delete flush: true

        redirect(action: "index")
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * Функция отображение image из БД
     * функционал убран - зачем качать картинки в БД?
     *
     * @param product
     */
    @Deprecated
    def showImage(Product product) {
        logger.debug(product)

        response.outputStream << product.image
        response.outputStream.flush()
    }

    /**
     * Функция передает данные таблицы Product (Для datatables.js)
     * Функционал убран. datatables - так себе решение при большом количестве записей в БД
     *
     * @return JSON
     */
    @Deprecated
    def getData() {
        logger.debug("getData()")

        def data = Product.list().collect {
            [
                    productId  : it.productId,
                    id         : it.id,
                    price      : it.price,
                    title      : it.title,
                    rating     : it.rating,
                    description: it.description?.length() > 200 ? (it.description.substring(0, 200) + "...") : it.description,
                    category   : it.category.getName(),
                    image      : it.image
            ]
        }
        render data as JSON
    }
}
