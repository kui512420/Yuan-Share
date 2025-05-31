package space.kuikui.utils;

import com.alibaba.excel.EasyExcel;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author kuikui
 * @date 2025/3/8 22:16
 */
@Component
public class ExportUtil {

    public void export(HttpServletResponse response,String fileName, List list ,Class clazz) throws IOException {
        // 设置响应头
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        // 使用 EasyExcel 写入数据到响应输出流
        EasyExcel.write(response.getOutputStream(), clazz)
                .registerConverter(new TimestampConverter())
                .sheet("信息")
                .doWrite(list);
    }
}
