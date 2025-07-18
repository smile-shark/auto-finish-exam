package com.smileShark.entity.school.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolTeacherCourseSimpleInfoResponse {

    @JsonProperty("eCourseID")
    private String eCourseID;
    @JsonProperty("eCourseName")
    private String eCourseName;
    private String lessonID;
    private String lessonName;
}
