package com.example.library_api.Controller

import com.example.library_api.Model.Category
import com.example.library_api.Repository.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController {

    @Autowired
    CategoryRepository categoryRepository

    @GetMapping("")
    List<Map<String, Object>> getCategories() {
        List<Category> categories = categoryRepository.findAll()
        return categories.collect { category ->
            [
                    id   : category.id,
                    name : category.name,
                    books : category.books.collect { book ->
                        [
                                id    : book.id,
                                name  : book.name,
                                isbn  : book.isbn
                        ]
                    }
            ]
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Object> getCategory(@PathVariable("id") Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id)
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Category not found"
            ])
        }

        Category category = optionalCategory.get()
        Map<String, Object> response = [
                id   : category.id,
                name : category.name,
                books: category.books.collect { book ->
                    [
                            id   : book.id,
                            name : book.name,
                            isbn : book.isbn
                    ]
                }
        ]

        return ResponseEntity.ok(response)
    }

    @PostMapping("")
    ResponseEntity<Object> createCategory(@RequestBody Map<String, String> request) {
        String name = request.get("name")

        if (name == null || name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body([
                    message: "Category name is required"
            ])
        }

        Category category = new Category(name: name)
        categoryRepository.save(category)

        return ResponseEntity.status(HttpStatus.CREATED).body([
                message: "Category created successfully",
                category: [
                        id   : category.id,
                        name : category.name
                ]
        ])
    }

    @PutMapping("/{id}")
    ResponseEntity<Object> updateCategory(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
        Optional<Category> optionalCategory = categoryRepository.findById(id)

        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Category not found"
            ])
        }

        Category category = optionalCategory.get()
        String name = request.get("name")

        if (name != null && !name.isEmpty()) {
            category.name = name
        }

        categoryRepository.save(category)

        return ResponseEntity.status(HttpStatus.OK).body([
                message: "Category updated successfully",
                category: [
                        id   : category.id,
                        name : category.name
                ]
        ])
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteCategory(@PathVariable("id") Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id)

        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Category not found"
            ])
        }

        categoryRepository.deleteById(id)

        return ResponseEntity.status(HttpStatus.OK).body([
                message: "Category deleted successfully"
        ])
    }
}