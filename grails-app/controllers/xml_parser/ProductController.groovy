package xml_parser

import grails.converters.JSON
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = false)
class ProductController {

    def productService

    def viewCounterService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Product.list(params), model: [productCount: Product.count()]
    }

    def show(Product product) {
        println("show()")

        //в отдельном потоке считаем количество просмотров
        Thread thread = new Thread(new Runnable() {
            @Override
            void run() {
                viewCounterService.incrementCounter(product)
                println("Счетчик увеличен в отдельном потоке")
            }
        })
        thread.start();

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

        if (product.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond product.errors, view: 'create'
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
        println("update()")

        if (product == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        productService.fixNumberParsing(product)

        if (product.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond product.errors, view: 'edit'
            return
        }

        if (!(product.title ==~ '^[а-яА-Я0-9 ]+$')) {
            println("не пройдена валидация!")

            transactionStatus.setRollbackOnly()
            product.errors.rejectValue('title', 'title.validation.error')
            respond product.errors, view: 'edit'
            return
        }



        product.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'Product'), product.id])
                redirect product
            }
            '*' { respond product, [status: OK] }
        }
    }

    @Transactional
    def delete(Product product) {

        if (product == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        product.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'product.label', default: 'Product'), product.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
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
     * Функция ипорта xml-файла
     * @return view
     */
    @Transactional
    def importXml() {
        def sourceXml = request.getFile('sourceXml')

        if (sourceXml.empty) {
            flash.message = 'file cannot be empty'
            render(view: 'index')
            return
        }

        def xmlContent = productService.getXmlContent(sourceXml)
        flash.message = productService.importProductsXml(xmlContent)

        render(view: 'index')
    }

    /**
     * Функция отображение image из БД
     * функционал убран - зачем качать картинки в БД?
     * @param product
     */
    @Deprecated
    def showImage(Product product) {
        println(product)

        response.outputStream << product.image
        response.outputStream.flush()
    }

    /**
     * Функция передает данные таблицы Product
     * TODO: не перйти ли на ручной постраничный показ?
     * @return JSON
     */
    def getData() {
        println("getData()")

        def data = Product.list().collect {
            [
                    productId  : it.productId,
                    id         : it.id,
                    price      : it.price,
                    title      : it.title,
                    rating     : it.rating,
                    description: it.description?.length() > 100 ? (it.description.substring(0, 100) + "...") : it.description,
                    category   : it.category.getName(),
                    image      : it.image
            ]
        }
        render data as JSON
    }
}
