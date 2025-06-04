import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.smileShark.Application;
import com.smileShark.constant.Constant;
import com.smileShark.entity.school.SchoolResult;
import com.smileShark.entity.school.request.SchoolLoginRequest;
import com.smileShark.entity.school.response.SchoolLoginResponse;
import com.smileShark.entity.school.response.SchoolTeacherCourseSimpleInfoResponse;
import com.smileShark.utils.RestTemplateUtil;
import com.smileShark.utils.TokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = Application.class)
public class RestTemplateTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private Constant constant;


    @Test
    public void teacherCourseInfoTest(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("LVID", "fe2bb098419f4474a621413c14eddafe");
        map.put("Key", null);
        map.put("TopCount", "99999");
        // 登录获取token
        SchoolLoginResponse schoolLoginResponse = restTemplateUtil.get(
                "https://zxsz.cqzuxia.com/connect/token",
                HttpMethod.POST,
                MediaType.APPLICATION_FORM_URLENCODED,
                new SchoolLoginRequest("lyp", "123456",
                        "c12abe723eda4b66af77015f2b572440",
                        "yHpq/AII2pBeUrUlSeMZhEs84gxSfQ/y+PyGBOmI6dh33EK6Za1VwHwz7uRRifUC"),
                SchoolLoginResponse.class,
                null,
                null
        );
//        SchoolResult schoolResult = restTemplateUtil.get(
//                constant.TEACHER_COURSE_LIST_URL,
//                HttpMethod.GET,
//                MediaType.APPLICATION_JSON,
//                null,
//                SchoolResult.class,
//                TokenUtil.montage(schoolLoginResponse),
//                map
//        );
//        String s = JSONUtil.toJsonStr(schoolResult.getData());
//        List<SchoolTeacherCourseSimpleInfoResponse> list = JSONUtil.toList(s, SchoolTeacherCourseSimpleInfoResponse.class);
//        for (SchoolTeacherCourseSimpleInfoResponse schoolTeacherCourseSimpleInfoResponse : list) {
//            System.out.println(schoolTeacherCourseSimpleInfoResponse);
//        }
        List<SchoolTeacherCourseSimpleInfoResponse> data = restTemplateUtil.get(
                constant.TEACHER_COURSE_LIST_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<List<SchoolTeacherCourseSimpleInfoResponse>>>() {
                },
                TokenUtil.montage(schoolLoginResponse),
                map
        ).getData();
        for (SchoolTeacherCourseSimpleInfoResponse schoolTeacherCourseSimpleInfoResponse : data) {
            System.out.println(schoolTeacherCourseSimpleInfoResponse);
        }

    }
    @Test
    public void utilLoginTest() {
        // 登录获取token
        SchoolLoginResponse schoolLoginResponse = restTemplateUtil.get(
                "https://zxsz.cqzuxia.com/connect/token",
                HttpMethod.POST,
                MediaType.APPLICATION_FORM_URLENCODED,
                new SchoolLoginRequest("lyp", "123456",
                        "c12abe723eda4b66af77015f2b572440",
                        "yHpq/AII2pBeUrUlSeMZhEs84gxSfQ/y+PyGBOmI6dh33EK6Za1VwHwz7uRRifUC"),
                SchoolLoginResponse.class,
                null,
                null
        );
        // 获取用户信息
        String s = restTemplateUtil.get(
                "https://zxsz.cqzuxia.com/teacherCertifiApi/api/TeacherCenter/GetTeacherInfo",
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                String.class,
                TokenUtil.montage(schoolLoginResponse),
                null
        );
        System.out.println("s = " + s);
    }
    @Test
    public void utilLoginTestStudent() {
        // 登录获取token
        SchoolLoginResponse schoolLoginResponse = restTemplateUtil.get(
                constant.STUDENT_LOGIN_URL,
                HttpMethod.POST,
                MediaType.APPLICATION_FORM_URLENCODED,
                new SchoolLoginRequest("500106200501259212", "030707",
                        constant.STUDENT_CLIENT_ID_PARAM,
                        constant.STUDENT_CLIENT_SECRET_PARAM),
                SchoolLoginResponse.class,
                null,
                null
        );
        // 获取用户信息
        String s = restTemplateUtil.get(
                constant.STUDENT_INFO_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                String.class,
                TokenUtil.montage(schoolLoginResponse),
                null
        );
        System.out.println("s = " + s);
    }

    @Test
    public void loginTest() {
        String loginUrl="https://zxsz.cqzuxia.com/connect/token";
        // 构建表单参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");  // 假设需要grant_type
        params.add("username", "lyp");
        params.add("password", "123456");
        params.add("code", "2341");
        params.add("tenant_id", "32");
        params.add("client_id", "c12abe723eda4b66af77015f2b572440");
        params.add("client_secret", "yHpq/AII2pBeUrUlSeMZhEs84gxSfQ/y+PyGBOmI6dh33EK6Za1VwHwz7uRRifUC");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 组合请求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

        System.out.println("状态码: " + response.getStatusCode());
        System.out.println("响应体: " + response.getBody());
    }
    @Test
    public void infoTest(){
        String infoUrl="https://zxsz.cqzuxia.com/teacherCertifiApi/api/TeacherCenter/GetTeacherInfo";
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 设置内容类型为 JSON
        headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0QUU1RkVFRUQ4ODhFMDAyRjM5ODcwODdGQjZBMDlFQ0UxQ0IzRThSUzI1NiIsInR5cCI6ImF0K2p3dCIsIng1dCI6IjVLNWY3dTJJamdBdk9ZY0lmN2FnbnM0Y3MtZyJ9.eyJuYmYiOjE3NDg2NzU3MDEsImV4cCI6MTc0ODY5MzcwMSwiaXNzIjoiaHR0cDovL3p4c3ouY3F6dXhpYS5jb20iLCJjbGllbnRfaWQiOiJjMTJhYmU3MjNlZGE0YjY2YWY3NzAxNWYyYjU3MjQ0MCIsInN1YiI6IjdiMDI0YTRlNmE4NjRjZjlhOWY0YWQ1MWEyNzMxYjY2IiwiYXV0aF90aW1lIjoxNzQ4Njc1NzAxLCJpZHAiOiJsb2NhbCIsInRlbmFudGlkIjoiMzIiLCJuaWNrbmFtZSI6Imx5cCIsIm5hbWUiOiLmnY7kvZHpuY8iLCJwaWN0dXJlIjoiaHR0cHM6Ly93cGltZy53YWxsc3Rjbi5jb20vZjc3ODczOGMtZTRmOC00ODcwLWI2MzQtNTY3MDNiNGFjYWZlLmdpZiIsImp0aSI6IkNGQkYzMDYyMjA3NDdEOEFENTQ0RTJCMzNGOTRGQzhFIiwiaWF0IjoxNzQ4Njc1NzAxLCJzY29wZSI6WyJhcGkxIiwib3BlbmlkIiwicHJvZmlsZSIsIm9mZmxpbmVfYWNjZXNzIl0sImFtciI6WyJwd2QiXX0.GmcrX_5ma4ecxJu7Tnyc2yemCfxl7yX8uiRMscK03llYjWoZsEnV1wXVuu_p4CkKgIbrHeJrHkWZGTKr_EKJ042rDyjZUSlfs96CxP8lxfNULrwfh_J699C_EtcjCoISEfJnfmY0dGhadEvqe_kSADQZHe4SOPZV_Lr0EQUVl4QxoU1UQTn80CDfgmZwDNcaGqEnCGbz4JMDwhq8fB_d0oG3PRvI7f10C8pz9Q4mzkko47al608QtMLy7A-WfeZ38C5hqeojqT_6WmD8EvVoSrTbhSwDy9tERMmUzbqE6HEvvoMq0we8Jnc8WSMOaTd8rQA9NaWVng8Ab2h3rHOFTQ");

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // 发送 POST 请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                infoUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 打印响应
        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
        System.out.println("Response Headers: " + responseEntity.getHeaders());
        System.out.println("Response Body: " + responseEntity.getBody());
    }
    @Test
    public void infoTest2(){
        String infoUrl="https://ai.cqzuxia.com/evaluation/api/studentevaluate/GetCourseInfoByCourseId";

        Map<String, String> params = MapUtil.of("CourseID","2669d02a18ef4b8e9613065aa87d1cb9");

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 设置内容类型为 JSON
        headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0QUU1RkVFRUQ4ODhFMDAyRjM5ODcwODdGQjZBMDlFQ0UxQ0IzRThSUzI1NiIsInR5cCI6ImF0K2p3dCIsIng1dCI6IjVLNWY3dTJJamdBdk9ZY0lmN2FnbnM0Y3MtZyJ9.eyJuYmYiOjE3NDg3NjA0MDMsImV4cCI6MTc0ODc3ODQwMywiaXNzIjoiaHR0cDovL2FpLmNxenV4aWEuY29tIiwiY2xpZW50X2lkIjoiNDMyMTVjZGZmMmQ1NDA3ZjhhZjA3NGQyZDdlNTg5ZWUiLCJzdWIiOiJlZmNlNDM4Nzk1NGU0MWVlYjA3YTBmNzYyZGNmNmVlNiIsImF1dGhfdGltZSI6MTc0ODc2MDQwMywiaWRwIjoibG9jYWwiLCJ0ZW5hbnRpZCI6IjMyIiwibmlja25hbWUiOiI1MDAxMDYyMDA1MDEyNTkyMTIiLCJuYW1lIjoi5YiY5rmYIiwicGljdHVyZSI6Imh0dHBzOi8vd3BpbWcud2FsbHN0Y24uY29tL2Y3Nzg3MzhjLWU0ZjgtNDg3MC1iNjM0LTU2NzAzYjRhY2FmZS5naWYiLCJqdGkiOiIwMkI1OTAyRDc0RTc1RThFNUIxMTI5MjM1MjVGMTcyNSIsImlhdCI6MTc0ODc2MDQwMywic2NvcGUiOlsiYXBpMSIsIm9wZW5pZCIsInByb2ZpbGUiLCJvZmZsaW5lX2FjY2VzcyJdLCJhbXIiOlsicHdkIl19.lxaW2nL8gp9iNZsf2__eBQh3XumVmpO6A1cEJYoa-R4G8_td6tLkcN06O2jJGke0yUtbdRtXNZwX_eklYp1Z555S-vOVw9Q9Kmt1CR9AJOFlgUD_1JCZ72DtL05XYrnF9U6JK7j3EXykbHtlTQVOVVhKdz-7RXSzWNZmEg6Ta1HPt_KdtzXUu_Lml0Zn_uQiz6OEVBBWjTw1tP11hNl3mwPGiAa1h5Cj-Y7ARbQ0dPUq_r_NEqY_-iOm8vcRkauTaCEBDjdRvUD3xbraSOb1c5xCPMnG8E2RMkB3LEbzb80wcPzHuMx_fxC8u30nHX8KXfh_UDbSfXD7psbz9rns6A");

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        System.out.println(restTemplateUtil.builderBrl(infoUrl, params));

        // 发送 POST 请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                restTemplateUtil.builderBrl(infoUrl, params),
                HttpMethod.GET,
                requestEntity,
                String.class
        );
        // 打印响应
        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
        System.out.println("Response Headers: " + responseEntity.getHeaders());
        System.out.println("Response Body: " + responseEntity.getBody());
    }
}
