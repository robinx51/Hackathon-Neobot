package ru.neostudy.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neostudy.datastorage.db.entity.Course;
import ru.neostudy.datastorage.db.repository.CoursesRepository;

@Service
public class CourseService {
    @Autowired
    public CoursesRepository coursesRepository;
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    public void createCourse(Course course) {
        logger.info("Добавление нового направления: {}", course.getCourseName());
        coursesRepository.save(course);
    }

    public void updateCourse(Course course) {
        logger.info("Обновление course с id: {}", course.getCourseId());
        if (coursesRepository.existsById(course.getCourseId())) {
            coursesRepository.save(course);
            logger.info("course обновлён успешно");
        } else {
            logger.error("course с id: {} не найден", course.getCourseId());
        }
    }

    public Course getCourseById(int courseId) {
        return coursesRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course с id: " + courseId + " не найден"));
    }
}