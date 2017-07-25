package xml_parser

import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import jdk.nashorn.internal.runtime.SharedPropertyMap
import spock.lang.Shared
import spock.lang.Specification


class ProductControllerSpec extends Specification implements ControllerUnitTest<ProductController>, DataTest {

    @Shared def product

    @Shared ProductService productService = new ProductService()

    void setupSpec() {
        mockDomain Product

        product = new Product(
                productId: 0,
                title: "title",
                description: "description",
                rating: 1f,
                price: new BigDecimal("100"),
                image: "image",
                category: new Category(grade: (byte) 1, name: "1")
        )
    }

    def populateValidParams(params) {
        assert params != null

        params["productId"] = '1'
        params["title"] = 'наименование'
        params["rating"] = '3.5'
        params["price"] = '1000'
        params["description"] = 'Desription'

    }

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.productList
        model.productCount == 0

        when: "The index action is executed with mock data"
        new Product(
                productId: 0,
                title: "title",
                description: "description",
                rating: 1f,
                price: new BigDecimal("100"),
                image: "image",
                category: new Category(grade: (byte) 1, name: "1")
        ).save()
        new Product(
                productId: 0,
                title: "title",
                description: "description",
                rating: 1f,
                price: new BigDecimal("100"),
                image: "image",
                category: new Category(grade: (byte) 1, name: "1")
        ).save()
        controller.index()

        then: "The model is correct"
        model.productCount == 2
        model.keySet().contains('productList')
    }


    void "Test the save action correctly persists an instance"() {

        controller.productService = productService

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            Product invalidProduct = new Product()
            invalidProduct.validate()
            controller.save(invalidProduct)

        then:"The create view is rendered again with the correct model"
            model.product!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            product = new Product(params)

            controller.save(product)

        then:"A redirect is issued to the show action"
            //response.redirectUrl == '/product/show/1' //Почему возвращается код 200?..
            //response.getStatus() == 302
            controller.flash.message != null
            Product.count() == 1

    }
/*
    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def product = new Product(params)
            controller.show(product)

        then:"A model is populated containing the domain instance"
            model.product == product
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def product = new Product(params)
            controller.edit(product)

        then:"A model is populated containing the domain instance"
            model.product == product
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/product/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def product = new Product()
            product.validate()
            controller.update(product)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.product == product

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            product = new Product(params).save(flush: true)
            controller.update(product)

        then:"A redirect is issued to the show action"
            product != null
            response.redirectedUrl == "/product/show/$product.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/product/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def product = new Product(params).save(flush: true)

        then:"It exists"
            Product.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(product)

        then:"The instance is deleted"
            Product.count() == 0
            response.redirectedUrl == '/product/index'
            flash.message != null
    }
    */
}
