package xml_parser

import grails.gorm.transactions.Transactional

@Transactional
class CategoryService {

    void initMockData() {
        println("initMockData()")

        def categories = Category.list();
        def mockData = new ArrayList<Category>();

        mockData.add(new Category(grade: (byte) 0, name: "Плохой"));
        mockData.add(new Category(grade: (byte) 1, name: "Хороший"));
        mockData.add(new Category(grade: (byte) 2, name: "Отличный"));

        for (Category category : mockData){
            if (!categories.contains(category)) {
                println("category ${category} not found! Creating...")
                category.save(flush: true)
            } else
                println("category ${category} was found")
        }
    }

    Category getByRating(Float rating) {
        println("getByRating()")

        if (Float.compare(rating, 3f) <= 0) {
            println("${rating} - относим к категории Плохая")
            return Category.findByGrade((byte)0)
        } else if (Float.compare(rating, 3f) > 0 && Float.compare(rating, 4f) <= 0) {
            println("${rating} - относим к категории Хорошая")
            return Category.findByGrade((byte)1)
        } else if (Float.compare(rating, 4f) > 0 && Float.compare(rating, 5f) <= 0) {
            println("${rating} - относим к категории Отличная")
            return Category.findByGrade((byte)2)
        }

        return Category.findByGrade((byte)0)
    }
}
