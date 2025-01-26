package ru.neostudy.apiservice.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Course {
    private int courseId;
    private String courseName;
}
