package com.example.library_api.Repository

import com.example.library_api.Model.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository extends JpaRepository<Category, Long> {}