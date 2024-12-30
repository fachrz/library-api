package com.example.library_api.Repository

import com.example.library_api.Model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository extends JpaRepository<Book, Long> {}
