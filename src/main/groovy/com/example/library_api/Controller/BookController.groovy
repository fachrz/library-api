package com.example.library_api.Controller

import com.example.library_api.Model.Book
import com.example.library_api.Model.Category
import com.example.library_api.Repository.BookRepository
import com.example.library_api.Repository.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController {

    @Autowired
    BookRepository bookRepository

    @Autowired
    CategoryRepository categoryRepository

    // GET all books
    @GetMapping("")
    ResponseEntity<Object> getBooks() {
        List<Book> books = bookRepository.findAll()

        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "No books found"
            ])
        }

        List<Map<String, Object>> response = books.collect { book ->
            [
                    id     : book.id,
                    name   : book.name,
                    isbn   : book.isbn,
                    category: [
                            id   : book.category.id,
                            name : book.category.name
                    ]
            ]
        }

        return ResponseEntity.ok(response)
    }

    // GET book by id
    @GetMapping("/{id}")
    ResponseEntity<Object> getBook(@PathVariable("id") Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id)

        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Book not found"
            ])
        }

        Book book = optionalBook.get()

        Map<String, Object> response = [
                id       : book.id,
                name     : book.name,
                isbn     : book.isbn,
                category : [
                        id   : book.category.id,
                        name : book.category.name
                ]
        ]

        return ResponseEntity.ok(response)
    }

    // POST new book
    @PostMapping("")
    ResponseEntity<Object> createBook(@RequestBody Book book) {
        Optional<Category> optionalCategory = categoryRepository.findById(book.category.id)

        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body([
                    message: "Category not found"
            ])
        }

        book.category = optionalCategory.get()
        Book savedBook = bookRepository.save(book)

        Map<String, Object> response = [
                id       : savedBook.id,
                name     : savedBook.name,
                isbn     : savedBook.isbn,
                category : [
                        id   : savedBook.category.id,
                        name : savedBook.category.name
                ]
        ]

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // PUT update book
    @PutMapping("/{id}")
    ResponseEntity<Object> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        Optional<Book> optionalBook = bookRepository.findById(id)

        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Book not found"
            ])
        }

        Book existingBook = optionalBook.get()
        existingBook.name = book.name
        existingBook.isbn = book.isbn
        if (book.category != null) {
            Optional<Category> optionalCategory = categoryRepository.findById(book.category.id)
            existingBook.category = optionalCategory.orElse(existingBook.category)
        }
        Book updatedBook = bookRepository.save(existingBook)

        Map<String, Object> response = [
                id       : updatedBook.id,
                name     : updatedBook.name,
                isbn     : updatedBook.isbn,
                category : [
                        id   : updatedBook.category.id,
                        name : updatedBook.category.name
                ]
        ]

        return ResponseEntity.ok(response)
    }

    // DELETE book
    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteBook(@PathVariable("id") Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id)

        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body([
                    message: "Book not found"
            ])
        }

        bookRepository.delete(optionalBook.get())

        return ResponseEntity.status(HttpStatus.OK).body([
                message: "Book deleted successfully"
        ])
    }
}