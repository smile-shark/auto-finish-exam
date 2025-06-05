//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.smileShark.Application;
//import com.smileShark.entity.Chapter;
//import com.smileShark.entity.Course;
//import com.smileShark.entity.QuestionAndAnswer;
//import com.smileShark.entity.Subsection;
//import com.smileShark.entity.school.response.SchoolStudentHaveCourseResponse;
//import com.smileShark.mapper.ChapterMapper;
//import com.smileShark.mapper.CourseMapper;
//import com.smileShark.mapper.QuestionAndAnswerMapper;
//import com.smileShark.mapper.SubsectionMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//@SpringBootTest(classes = Application.class)
//public class MapperTest {
//    @Autowired
//    private QuestionAndAnswerMapper questionAndAnswerMapper;
//    @Autowired
//    private CourseMapper courseMapper;
//    @Autowired
//    private ChapterMapper chapterMapper;
//    @Autowired
//    private SubsectionMapper subsectionMapper;
//    @Autowired
//    private SchoolStudentHaveCourseResponse.Course course;
//
//    /**
//     * 将答案中最后一位是L的将L去掉
//     */
//    @Test
//    public void test01(){
//        List<QuestionAndAnswer> questionAndAnswers = questionAndAnswerMapper.selectList(null);
//        for (QuestionAndAnswer questionAndAnswer : questionAndAnswers) {
//            if(questionAndAnswer.getAnswers().endsWith("L")){
//                System.out.println(questionAndAnswer.getAnswers());
//                questionAndAnswer.setAnswers(questionAndAnswer.getAnswers().substring(0,questionAndAnswer.getAnswers().length()-1));
//                System.out.println(questionAndAnswer.getAnswers());
//                questionAndAnswerMapper.updateById(questionAndAnswer);
//            }
//        }
//    }
//
//    /**
//     * 删除没有小节的章节，
//     * 删除没有章节的课程
//     */
//    @Test
//    public void test02(){
//        List<Chapter> chapters = chapterMapper.selectList(null);
//        for (Chapter chapter : chapters) {
//            if(subsectionMapper.selectCount(new LambdaQueryWrapper<Subsection>()
//                    .eq(Subsection::getChapterId,chapter.getChapterId()))==0){
//                chapterMapper.deleteById(chapter.getChapterId());
//            }
//        }
//        List<Course> courses = courseMapper.selectList(null);
//        for (Course course : courses) {
//            if(chapterMapper.selectCount(new LambdaQueryWrapper<Chapter>()
//                    .eq(Chapter::getCourseId,course.getCourseId()))==0){
//                courseMapper.deleteById(course.getCourseId());
//            }
//        }
//    }
//}
