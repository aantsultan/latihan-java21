package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.ImageRecognitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image Recognition", description = "The Image Recognition API is used for predict what kind of image is.")
public class ImageRecognitionController {

    private final ImageRecognitionService service;

    @GetMapping(value = "/image-recognition", produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadData(){
        return service.loadData();
    }

    @GetMapping(value = "/image-recognition/v2", produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadDataV2(){
        return service.loadDataV2();
    }

    @GetMapping(value = "/image-recognition/v3/{fileName}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadDataV3(@PathVariable("fileName") String fileName){
        return service.loadDataV3(fileName);
    }

}
