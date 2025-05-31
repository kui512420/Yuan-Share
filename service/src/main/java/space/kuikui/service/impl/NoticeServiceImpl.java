package space.kuikui.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.NoticeMapper;
import space.kuikui.service.NoticeService;
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public int count() {
        return noticeMapper.count();
    }
}
