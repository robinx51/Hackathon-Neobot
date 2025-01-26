package ru.neostudy.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neostudy.datastorage.db.entity.Course;

@Repository
public interface CoursesRepository extends JpaRepository<Course, Integer> {
}