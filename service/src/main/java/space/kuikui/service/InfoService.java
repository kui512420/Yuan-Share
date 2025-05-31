package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import space.kuikui.pojo.Article;
import space.kuikui.pojo.Info;

import java.util.List;
import java.util.Map;

public interface InfoService {
    public PageInfo<Map<String, Object>> getInfoAll(int pagenow, int pagesize);
    public List<Map<String,Object>> getInfo();
    public boolean setInfo(Info info);
    public boolean delInfo();
    public int count();
}
