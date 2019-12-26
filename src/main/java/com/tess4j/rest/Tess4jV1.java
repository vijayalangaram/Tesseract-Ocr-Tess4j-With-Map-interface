package com.tess4j.rest;

import com.tess4j.rest.model.Image;
import com.tess4j.rest.model.Status;
import com.tess4j.rest.model.Text;
import com.tess4j.rest.mongo.ImageRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SpringBootApplication
@RestController
public class Tess4jV1 {

    private Logger LOGGER = LoggerFactory.getLogger(Tess4jV1.class);

    @Autowired
    private ImageRepository repository;

    @RequestMapping(value = "ocr/ping", method = RequestMethod.GET)
    public Status ping() throws Exception {
        return new Status("OK");
    }

    @RequestMapping(value = "ocr/v1/convert", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Text convertImageToText(@RequestBody final Image image) throws Exception {

        File tmpFile = File.createTempFile("ocr_image", image.getExtension());
        try {
            FileUtils.writeByteArrayToFile(tmpFile, Base64.decodeBase64(image.getImage()));
            Tesseract tesseract = new Tesseract(); // JNA Interface Mapping
            String imageText = tesseract.doOCR(tmpFile);
            LOGGER.debug("OCR Image Text = " + imageText);
            return new Text(imageText);
        } catch (Exception e) {
            LOGGER.error("Exception while converting/uploading image: ", e);
            throw new TesseractException();
        } finally {
            tmpFile.delete();
        }   
    }
        
//    
//    @RequestMapping(value = "ocr/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public Status doOcr(@RequestBody Image image) throws Exception 
//    {
//        try {            
//            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(image.getImage()));
//            Tesseract tesseract = new Tesseract(); // JNA Interface Mapping
//            String imageText = tesseract.doOCR(ImageIO.read(bis));
//            image.setText(imageText);
//            repository.save(image);
//            LOGGER.debug("OCR Result = " + imageText);
//        } catch (Exception e) {
//            LOGGER.error("TessearctException while converting/uploading image: ", e);
//            throw new TesseractException();
//        }
//        return new Status("success");    }
    
    
    
    @RequestMapping(value = "ocr/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Status doOcr(@RequestBody Map<String, Image>  image) throws Exception 
    {
        try {            
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(((Image) image).getImage()));
            Tesseract tesseract = new Tesseract(); // JNA Interface Mapping
            String imageText = tesseract.doOCR(ImageIO.read(bis));
            ((Image) image).setText(imageText);
            repository.save(image);
            LOGGER.debug("OCR Result = " + imageText);
        } catch (Exception e) {
            LOGGER.error("TessearctException while converting/uploading image: ", e);
            throw new TesseractException();
        }
        return new Status("success");    }
    
    @RequestMapping(value = "ocr/v1/images/users/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Image> getUserImages(@PathVariable String userId) throws Exception {
        List<Image> userImages = new ArrayList<>();
        try {
            userImages = repository.findByUserId(userId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred finding image for userId: {} ", userId, e);
            throw new Exception();
        }
        return userImages;
    }

    
    public static void main(String[] args) {
        SpringApplication.run(Tess4jV1.class, args);
    }
    
    
}

//// modified

//public Text convertImageToText(@RequestBody final  Map<String, Image>  image) throws Exception {
//File tmpFile = File.createTempFile("ocr_image", ((Image) image).getExtension());
//try {
//    FileUtils.writeByteArrayToFile(tmpFile, Base64.decodeBase64(((Image) image).getImage()));
//    Tesseract tesseract = new Tesseract(); // JNA Interface Mapping
//    String imageText = tesseract.doOCR(tmpFile);
//    LOGGER.debug("OCR Image Text = " + imageText);
//    return new Text(imageText);
//} catch (Exception e) {
//    LOGGER.error("Exception while converting/uploading image: ", e);
//    throw new TesseractException();
//} finally {
//    tmpFile.delete();
//}    }

////// modified    MAP 1   
//
//@RequestMapping(value = "ocr/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
//public Status doOcr(@RequestBody  Map<String, Image>  image) throws Exception
//{
//try {      
//    ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(((Image) image).getImage()));
//    Tesseract tesseract = new Tesseract(); // JNA Interface Mapping
//    String imageText = tesseract.doOCR(ImageIO.read(bis));
//    ((Image) image).setText(imageText);
//    repository.save(image);
//    LOGGER.debug("OCR Result = " + imageText);
//} catch (Exception e) {
//    LOGGER.error("TessearctException while converting/uploading image: ", e);
//    throw new TesseractException();
//}
//return new Status("success");
//}


/////MAP INTERFACE 2    
//@RequestMapping(value = "ocr/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
// public Map<String, Image>  doOcr(@RequestBody Image image) throws Exception {    
////   HttpServletRequest request, HttpServletResponse response  
//try {
//   
//       ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(image.getImage()));
//       Tesseract tesseract = new Tesseract(); 
//       String imageText = tesseract.doOCR(ImageIO.read(bis));
//       image.setText(imageText);
//       repository.save(image);
//       LOGGER.debug("OCR Result = " + imageText);
//   } catch (Exception e) {
//       LOGGER.error("TessearctException while converting/uploading image: ", e);
//       throw new TesseractException();
//   }
//   return (Map<String, Image>) new Status("success");
//   }
