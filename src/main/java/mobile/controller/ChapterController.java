package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.ChapterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;


}
